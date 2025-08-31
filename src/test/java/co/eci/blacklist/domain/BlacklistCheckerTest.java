package co.eci.blacklist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import co.eci.blacklist.infrastructure.HostBlackListsDataSourceFacade;

class BlacklistCheckerTest {

    @Test
    void testTrustedIp() {
        HostBlackListsDataSourceFacade facade = HostBlackListsDataSourceFacade.getInstance();
        Policies policies = new Policies();
        policies.setAlarmCount(5);

        BlacklistChecker checker = new BlacklistChecker(facade, policies);

        MatchResult result = checker.checkHost("127.0.0.1", 4);

        assertTrue(result.trustworthy());
        assertEquals("127.0.0.1", result.ip());
        assertEquals(0, result.checkedServers());
        assertEquals(facade.getRegisteredServersCount(), result.totalServers());
        assertEquals(0, result.matches().size());
        assertTrue(result.elapsedMs() >= 0);
        assertEquals(4, result.threads());
    }

    @Test
    void testNotTrustedIp() {
        HostBlackListsDataSourceFacade facade = HostBlackListsDataSourceFacade.getInstance();
        Policies policies = new Policies();
        policies.setAlarmCount(3); // bajo para forzar "no confiable"

        BlacklistChecker checker = new BlacklistChecker(facade, policies);

        MatchResult result = checker.checkHost("200.24.34.55", 4);

        assertFalse(result.trustworthy());
        assertEquals("200.24.34.55", result.ip());
        assertTrue(result.checkedServers() >= 3);
        assertEquals(facade.getRegisteredServersCount(), result.totalServers());
        assertFalse(result.matches().isEmpty());
        assertTrue(result.elapsedMs() >= 0);
        assertEquals(4, result.threads());
    }
}
