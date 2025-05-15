package controller;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repositories;
import repository.StationRepository;
import domain.Station.Orientation;
import controller.AuthController;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static domain.Station.Orientation.NE;
import static org.junit.jupiter.api.Assertions.*;

class CreateStationControllerTest {

    CreateStationController controller;
    StationRepository stationRepo;
    User user;
    AuthController authController = new AuthController();
    @BeforeEach
    void setUp() {
        controller = new CreateStationController();
        stationRepo = Repositories.getInstance().getStationRepository();
        user = new User("Player 1", "Player@this.app", "pwd", authController.ROLE_PLAYER, new Budget(1000));
    }
    @Test
    void testInvalidType() {
        Map map = new Map("Map1", 10, 10);
        var err = controller.buildStation("invalid", map, 5, 5, NE);
        assertEquals(CreateStationController.Error.INVALID_TYPE, err);
    }

    @Test
    void testOverbuild(){
        Map map = new Map("Map1", 10, 10);
        CurrentUser.getInstance().setUser(user);
        Station existing = new Station(Station.Type.DEPOT, "Ex", "Map1", "Player@this.app", 50, 100, 0, 4, 4, null);
        stationRepo.addStation(existing);
        var err = controller.buildStation("DEPOT", map, 4, 4, null);
        assertEquals(CreateStationController.Error.OVERBUILD, err);
    }

    @Test
    void testInsufficientFunds(){
        Map map = new Map("Map1", 10, 10);
        CurrentUser.getInstance().setUser(user);
        user.setBudget(new Budget(user.getBudgetValue() - 1000));
        var err = controller.buildStation("DEPOT", map, 4, 4, null);
        assertEquals(CreateStationController.Error.INSUFFICIENT_FUNDS, err);

    }

    @Test
    void testInvalidCoordinates() {
        Map map = new Map("Map1", 4, 4);
        CurrentUser.getInstance().setUser(user);
        var err = controller.buildStation("TERMINAL", map, 1, 1, null);
        assertEquals(CreateStationController.Error.INVALID_COORDINATES, err);
    }

    @Test
    void testSuccessfulBuid(){
        Map map = new Map("Map2", 10, 10);
        CurrentUser.getInstance().setUser(user);
        var err = controller.buildStation("DEPOT", map, 4, 4, null);
        assertEquals(CreateStationController.Error.OK, err);
        Optional<Station> station = stationRepo.getStations().stream().filter(s -> s.getType() == Station.Type.DEPOT).findFirst();
        assertTrue(station.isPresent());
        assertEquals(4, station.get().getX());
        assertEquals(4, station.get().getY());
        assertEquals(NE, station.get().getOrientation());
    }

    @Test
    void testSuccessfulBuildCustomCost(){
        Map map = new Map("Map3", 10, 10);
        CurrentUser.getInstance().setUser(user);
        var err = controller.buildStation("DEPOT", map, 4, 4, NE,120);
        assertEquals(CreateStationController.Error.OK, err);
        Optional<Station> station = stationRepo.getStations().stream().filter(s -> s.getType() == Station.Type.DEPOT).findFirst();
        assertTrue(station.isPresent());
        assertEquals(4, station.get().getX());
        assertEquals(4, station.get().getY());
        assertEquals(NE, station.get().getOrientation());
    }

    @Test
    void testGetStations() {
        CurrentUser.getInstance().setUser(user);
        Map map = new Map("Map1", 10, 10);
        controller.buildStation("DEPOT", map, 4, 4, null);
        List<String> expected = List.of(
                "Unknown-DEPOT DEPOT (4,4)",
                "Unknown-DEPOT DEPOT (4,4)"
        );
        List<String> actual = controller.getStations().stream()
                .map(Station::toString)
                .collect(Collectors.toList());

        assertEquals(expected, actual);
    }

    @Test
    void testGetRepo(){
        StationRepository stationRepo2 = controller.getStationRepository();
        assertEquals(stationRepo, stationRepo2);

    }

}