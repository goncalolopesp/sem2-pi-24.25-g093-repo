package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class RailwayLineTest {
    private Station fromStation;
    private Station toStation;
    private RailwayLineType type;
    private Scenario scenario;
    private Map map;
    private RailwayLine railwayLine;
    private static final int DISTANCE = 100;
    private static final String OWNER_MAIL = "teste@exemplo.com";
    private static final String STATION1_NAME = "Estação A";
    private static final String STATION2_NAME = "Estação B";
    private static final String MAP_NAME = "Mapa Test";
    private static final String SCENARIO_NAME = "Cenário Test";

    @BeforeEach
    void setUp() {
        map = new Map(MAP_NAME, 100,100,1);
        scenario = new Scenario("TestScenario", MAP_NAME, new int[]{1870, 1880}, 1, Collections.singletonList("steam"), Collections.singletonList("none"), 1, 1);
        fromStation = new Station(Station.Type.DEPOT,STATION1_NAME, MAP_NAME, SCENARIO_NAME,"player@this.app", 100, 200, 30, 45, null);
        toStation = new Station(Station.Type.DEPOT,STATION2_NAME, MAP_NAME, SCENARIO_NAME, "player@this.app", 100, 200, 23, 45, null);
        type = RailwayLineType.SINGLE;
        railwayLine = new RailwayLine(fromStation, toStation, type, DISTANCE, OWNER_MAIL, scenario, map);
    }

    @Test
    void testConstructorAndGetters() {
        assertAll(
                () -> assertEquals(fromStation, railwayLine.getFromStation()),
                () -> assertEquals(toStation, railwayLine.getToStation()),
                () -> assertEquals(type, railwayLine.getType()),
                () -> assertEquals(DISTANCE, railwayLine.getDistance()),
                () -> assertEquals(OWNER_MAIL, railwayLine.getOwnerMail()),
                () -> assertEquals(scenario, railwayLine.getScenario()),
                () -> assertEquals(map, railwayLine.getMap())
        );
    }

    @Test
    void testGeneratedId() {
        String expectedId = STATION1_NAME + "->" + STATION2_NAME + "_" + MAP_NAME + "_" + SCENARIO_NAME;
        assertEquals(expectedId, railwayLine.getId());
    }

    @Test
    void testConstructorWithNullFromStation() {
        assertThrows(NullPointerException.class, () -> {
            new RailwayLine(null, toStation, type, DISTANCE, OWNER_MAIL, scenario, map);
        });
    }

    @Test
    void testConstructorWithNullToStation() {
        assertThrows(NullPointerException.class, () -> {
            new RailwayLine(fromStation, null, type, DISTANCE, OWNER_MAIL, scenario, map);
        });
    }
}
