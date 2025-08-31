package co.eci.blacklist.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;
import co.eci.blacklist.labs.part2.ParallelBlacklistSearchThread;

public class BlacklistChecker {

    private static final Logger logger = Logger.getLogger(BlacklistChecker.class.getName());

    private final HostBlackListsDataSourceFacade facade;
    private final Policies policies;

    public BlacklistChecker(HostBlackListsDataSourceFacade facade, Policies policies) {
        this.facade = Objects.requireNonNull(facade);
        this.policies = Objects.requireNonNull(policies);
    }

    public MatchResult checkHost(String ip, int nThreads) {
        int threshold = policies.getAlarmCount();
        int total = facade.getRegisteredServersCount();

        long start = System.currentTimeMillis();
        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger globalCounter = new AtomicInteger(0);
        List<Integer> matches = new ArrayList<>();

        int threadsCount = Math.max(1, nThreads);
        int chunk = Math.max(1, total / threadsCount);

        ParallelBlacklistSearchThread[] threads = new ParallelBlacklistSearchThread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
                final int startIdx = i * chunk;
                final int endIdx = (i == threadsCount - 1) ? total : Math.min(total, (i + 1) * chunk);
                threads[i] = new ParallelBlacklistSearchThread(ip, startIdx, endIdx, facade, globalCounter, stop, threshold);
        }
        
        for (ParallelBlacklistSearchThread t : threads) {
            t.start();
        }

        for (ParallelBlacklistSearchThread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Error waiting for thread", e);
            }
        }
        for (ParallelBlacklistSearchThread t : threads) {
            matches.addAll(t.getBlackListOccurrences());

        }

        int totalCoincidences = globalCounter.get();
        boolean trustworthy = totalCoincidences < threshold;
        logger.info("Checked blacklists: " + totalCoincidences + " of " + total);
        if (trustworthy) {
            facade.reportAsTrustworthy(ip);
        } else {
            facade.reportAsNotTrustworthy(ip);
        }
        long elapsed = System.currentTimeMillis() - start;
        return new MatchResult(ip, trustworthy, List.copyOf(matches), totalCoincidences, total, elapsed, threadsCount);
    }
}
