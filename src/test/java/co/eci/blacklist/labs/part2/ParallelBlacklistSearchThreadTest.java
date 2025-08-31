package co.eci.blacklist.labs.part2;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;

class ParallelBlacklistSearchThreadTest {

    @Test
    void testRunFindsBlacklistedServers() {
        String ip = "192.168.0.1";
        HostBlackListsDataSourceFacade facade = HostBlackListsDataSourceFacade.getInstance();
        facade.clear(ip); 
        facade.seed(ip, List.of(1, 3)); 

        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger counter = new AtomicInteger(0);
        int threshold = 5;

        ParallelBlacklistSearchThread thread =
                new ParallelBlacklistSearchThread(ip, 0, 5, facade, counter, stop, threshold);
        thread.run();

        List<Integer> found = thread.getBlackListOccurrences();
        assertEquals(2, thread.getMatchCount());
        assertTrue(found.contains(1));
        assertTrue(found.contains(3));
    }

    @Test
    void testStopsWhenThresholdReached() {
        String ip = "10.0.0.1";
        HostBlackListsDataSourceFacade facade = HostBlackListsDataSourceFacade.getInstance();
        
        // preparar: limpiar y sembrar TODOS los servidores como blacklists
        facade.clear(ip);
        facade.seed(ip, java.util.stream.IntStream.range(0, 100).boxed().toList());

        AtomicBoolean stop = new AtomicBoolean(false);
        AtomicInteger counter = new AtomicInteger(0);

        ParallelBlacklistSearchThread thread =
                new ParallelBlacklistSearchThread(ip, 0, 100, facade, counter, stop, 3);

        thread.run();

        assertTrue(counter.get() >= 3);
        assertTrue(stop.get());
    }
}
