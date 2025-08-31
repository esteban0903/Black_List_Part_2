package co.eci.blacklist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;

public class BlacklistCheckerTest {

    @Test
    void earlyStopShouldAvoidScanningAllServers() {
        Policies policies = new Policies();
        policies.setAlarmCount(5);
        HostBlackListsDataSourceFacade facade = HostBlackListsDataSourceFacade.getInstance();
        String ip = "200.24.34.55";
        BlacklistChecker checker = new BlacklistChecker(facade, policies);
        MatchResult result = checker.checkHost(ip, Math.max(2, Runtime.getRuntime().availableProcessors()));

        assertNotNull(result);
        assertEquals(ip, result.ip());
        assertFalse(result.trustworthy(), "Should be NOT trustworthy when threshold reached");
        assertTrue(result.matches().size() >= policies.getAlarmCount());
        assertTrue(result.checkedServers() < result.totalServers(), "Should stop early and not scan all servers");
    }
}
