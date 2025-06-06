package controller;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repositories;
import repository.StationRepository;

import static domain.Station.Orientation.NE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class CreateStationControllerTest {

    private CreateStationController controller;
    private StationRepository stationRepo;
    private User user;

    private Map map;
    private Scenario scenario;
    private Budget budget;
    @BeforeEach
    void setUp() {
        stationRepo = Repositories.getInstance().getStationRepository();
        stationRepo.removeStationsByMapScenarioAndOwner("", "", "");

        map = new Map("Map1", 100, 100,1);
         scenario = new Scenario("TestScenario", "Map1", new int[]{}, List.of(), List.of());
        controller = new CreateStationController();
        user = new User("Player 1", "player@this.app", "pwd", "ROLE_PLAYER");
        budget = new Budget (1000, map,scenario, user);
    }

    @Test
    void testInvalidType() {
        var err = controller.buildStation("invalid", map, scenario, 5, 5, NE,budget);
        assertEquals(CreateStationController.Error.INVALID_TYPE, err);
    }

    @Test
    void testOverbuild() {
        CurrentUser.getInstance().setUser(user);
        Station existing = new Station(Station.Type.DEPOT, "Ex", "Map1", "S", "player@this.app", 50, 100, 0, 4, 4, null);
        stationRepo.addStation(existing);

        var err = controller.buildStation("DEPOT", map, scenario, 4, 4, null,budget);
        assertEquals(CreateStationController.Error.OVERBUILD, err);
    }

    @Test
    void testInvalidCoordinates() {
        CurrentUser.getInstance().setUser(user);
        var err = controller.buildStation("TERMINAL", map, scenario, 0, 5, null,budget);
        assertEquals(CreateStationController.Error.INVALID_COORDINATES, err);
    }

    @Test
    void testSuccessfulBuild() {
        CurrentUser.getInstance().setUser(user);

        var err = controller.buildStation("DEPOT", map, scenario,90 , 90, NE,budget);
        assertEquals(CreateStationController.Error.OK, err);

        Optional<Station> st = stationRepo.getStations().stream()
                .filter(s -> s.getType() == Station.Type.DEPOT
                        && s.getMapName() == "Map1"
                        && s.getScenarioName() == "TestScenario"
                        && s.getX() == 90 && s.getY() == 90
                        && s.getOrientation() == NE)
                .findFirst();
        assertTrue(st.isPresent());
    }

    @Test
    void testGetStations() {
        CurrentUser.getInstance().setUser(user);
        controller.buildStation("DEPOT", map, scenario, 2, 2, null,budget);

        List<String> expected = List.of(
                "Unknown-DEPOT DEPOT (2,2)"
        );
        List<String> actual = controller.getStations().stream()
                .map(Station::toString)
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void testBuildStationUpgrade_Success() {
        CurrentUser.getInstance().setUser(user);
        stationRepo.removeStationsByMapScenarioAndOwner(map.getMapName(), scenario.getScenarioName(), user.getEmail());

        int initialBudget = budget.getBudget();
        int upgradeCost = 150;

        CreateStationController.Error err = controller.buildStation(
                "TERMINAL",
                map,
                scenario,
                20,
                25,
                null,
                budget,
                upgradeCost
        );
        assertEquals(CreateStationController.Error.OK, err);
        assertEquals(initialBudget - upgradeCost, budget.getBudget());
        Optional<Station> stOpt = stationRepo.getStations().stream()
                .filter(s ->
                        s.getType() == Station.Type.TERMINAL &&
                                s.getMapName().equals(map.getMapName()) &&
                                s.getScenarioName().equals(scenario.getScenarioName()) &&
                                s.getOwnerPlayer().equals(user.getEmail()) &&
                                s.getX() == 20 &&
                                s.getY() == 25
                )
                .findFirst();
        assertTrue(stOpt.isPresent(), "The station should have been upgraded");
    }


    @Test
    void testGetRepo() {
        StationRepository repo2 = controller.getStationRepository();
        assertSame(stationRepo, repo2);
    }
}
