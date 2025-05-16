package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScenarioTest {
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        scenario = new Scenario("TestScenario", "TestMap", new int[]{1870, 1880}, Collections.singletonList("steam"), Collections.singletonList("none"));
    }

    @Test
    void getScenarioName() {
        assertEquals("TestScenario", scenario.getScenarioName());
    }

    @Test
    void getSelectedMap() {
        assertEquals("TestMap", scenario.getSelectedMap());
    }

    @Test
    void getTimeRestrictions() {
        assertArrayEquals(new int[]{1870, 1880}, scenario.getTimeRestrictions());
    }

    @Test
    void getTechnologicalRestrictions() {
        assertEquals(Collections.singletonList("steam"), scenario.getTechnologicalRestrictions());
    }

    @Test
    void getHistoricalRestrictions() {
        assertEquals(Collections.singletonList("none"), scenario.getHistoricalRestrictions());
    }
}
