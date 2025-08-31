package co.eci.blacklist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PoliciesTest {
    @Test
    void testAlarmCountGetterSetter() {
        Policies policies = new Policies();
        policies.setAlarmCount(7);
        assertEquals(7, policies.getAlarmCount());
    }
}