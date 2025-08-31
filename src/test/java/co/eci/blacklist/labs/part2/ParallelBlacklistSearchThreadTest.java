package co.eci.blacklist.labs.part2;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParallelBlacklistSearchThreadTest {
    @Test
    void testRunFindsBlacklistedServers() {
        String ip = "192.168.0.1";
        int start = 0;
        int end = 4;
        HostBlackListsDataSourceFacade facade = mock(HostBlackListsDataSourceFacade.class);
        when(facade.isInBlackListServer(0, ip)).thenReturn(false);
        when(facade.isInBlackListServer(1, ip)).thenReturn(true);
        when(facade.isInBlackListServer(2, ip)).thenReturn(false);
        when(facade.isInBlackListServer(3, ip)).thenReturn(true);
        when(facade.isInBlackListServer(4, ip)).thenReturn(false);
        AtomicBoolean stop = new AtomicBoolean(false);
        int threshold = 5; 

        ParallelBlacklistSearchThread thread = new ParallelBlacklistSearchThread(ip, start, end, facade, stop, threshold);
        thread.run();
        List<Integer> found = thread.getBlackListOccurrences();
        assertEquals(2, thread.getMatchCount());
        assertTrue(found.contains(1));
        assertTrue(found.contains(3));
    }
}
