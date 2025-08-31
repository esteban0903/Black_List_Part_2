package co.eci.blacklist.domain;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;
import co.eci.blacklist.labs.part2.ParallelBlacklistSearchThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

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
        int checked = 0;
        AtomicBoolean stop = new AtomicBoolean(false);
        List<Integer> matches = Collections.synchronizedList(new ArrayList<>());

        int threadsCount = Math.max(1, nThreads);
        int chunk = Math.max(1, total / threadsCount);

        ParallelBlacklistSearchThread[] threads = new ParallelBlacklistSearchThread[nThreads];
        for (int i = 0; i < threadsCount; i++) {
                final int startIdx = i * chunk;
                final int endIdx = (i == threadsCount - 1) ? total : Math.min(total, (i + 1) * chunk);
                threads[i] = new ParallelBlacklistSearchThread(ip, startIdx, endIdx, facade, stop, threshold);
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
            checked += t.getTotalChecked();
        }

        int found = matches.size();
        boolean trustworthy = found < threshold;
        logger.info("Checked blacklists: " + checked + " of " + total);
        if (trustworthy) {
            facade.reportAsTrustworthy(ip);
        } else {
            facade.reportAsNotTrustworthy(ip);
        }
        long elapsed = System.currentTimeMillis() - start;
        return new MatchResult(ip, trustworthy, List.copyOf(matches), checked, total, elapsed, threadsCount);
    }
}
