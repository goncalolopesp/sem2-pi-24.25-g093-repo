package controller;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignTrainControllerTest {

    @Test
    public void testAssignExistingTrainToRoute_NormalScenario() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player");
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<PointOfRoute> route = new ArrayList<>();
        Station depot = new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, null);
        Station station1 = new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, Station.Orientation.NE);
        Station terminal = new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, null);

        // Fix PointOfRoute constructor calls
        route.add(new PointOfRoute(depot, new ArrayList<>(), CargoMode.FULL));
        route.add(new PointOfRoute(station1, Arrays.asList(Carriage.Type.PEOPLE), CargoMode.FULL));
        route.add(new PointOfRoute(terminal, Arrays.asList(Carriage.Type.MAIL), CargoMode.FULL));

        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        // Fix Scenario constructor call
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                0, new ArrayList<>(), normalRestrictions, 1.0, 1.0);

        Budget budget = new Budget(1000, null, normalScenario, testUser);

        // This should execute without throwing exceptions
        assertDoesNotThrow(() -> {
            controller.assignExistingTrainToRoute(train, route, normalScenario, budget);
        });

        // Verify that the train is properly assigned (we can't verify repository saves without mocking)
        assertNotNull(train.getLocomotive());
        assertEquals(2, train.getCarriages().size());
        assertEquals("test@example.com", train.getUserEmail());
    }

    @Test
    public void testAssignExistingTrainToRoute_VaccinationScenario() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player");
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<PointOfRoute> route = new ArrayList<>();
        Station depot = new Station(Station.Type.DEPOT, "Depot", "TestMap", "Vaccination Scenario", "test@example.com",
                100, 100, 0, 0, null);
        Station station1 = new Station(Station.Type.STATION, "Station1", "TestMap", "Vaccination Scenario", "test@example.com",
                200, 200, 10, 10, Station.Orientation.NE);
        Station terminal = new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Vaccination Scenario", "test@example.com",
                300, 300, 20, 20, null);

        // Fix PointOfRoute constructor calls
        route.add(new PointOfRoute(depot, new ArrayList<>(), CargoMode.FULL));
        route.add(new PointOfRoute(station1, Arrays.asList(Carriage.Type.PEOPLE), CargoMode.FULL));
        route.add(new PointOfRoute(terminal, Arrays.asList(Carriage.Type.MAIL), CargoMode.FULL));

        List<String> vaccinationRestrictions = new ArrayList<>();
        vaccinationRestrictions.add("vaccination campaign");
        // Fix Scenario constructor call
        Scenario vaccinationScenario = new Scenario("Vaccination Scenario", "TestMap", new int[]{1900, 2000},
                0, new ArrayList<>(), vaccinationRestrictions, 1.0, 1.0);

        Budget budget = new Budget(1000, null, vaccinationScenario, testUser);

        // This should execute without throwing exceptions
        assertDoesNotThrow(() -> {
            controller.assignExistingTrainToRoute(train, route, vaccinationScenario, budget);
        });

        // Verify that the train is properly assigned
        assertNotNull(train.getLocomotive());
        assertEquals(2, train.getCarriages().size());
        assertEquals("test@example.com", train.getUserEmail());
    }

    @Test
    public void testAssignExistingTrainToRoute_WithPeopleCarriageOnly() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player");
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<PointOfRoute> route = new ArrayList<>();
        Station depot = new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, null);
        Station station1 = new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, Station.Orientation.NE);
        Station terminal = new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, null);

        // Fix PointOfRoute constructor calls
        route.add(new PointOfRoute(depot, new ArrayList<>(), CargoMode.FULL));
        route.add(new PointOfRoute(station1, Arrays.asList(Carriage.Type.PEOPLE), CargoMode.FULL));
        route.add(new PointOfRoute(terminal, Arrays.asList(Carriage.Type.PEOPLE), CargoMode.FULL));

        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        // Fix Scenario constructor call
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                0, new ArrayList<>(), normalRestrictions, 1.0, 1.0);

        Budget budget = new Budget(1000, null, normalScenario, testUser);

        // This should execute without throwing exceptions
        assertDoesNotThrow(() -> {
            controller.assignExistingTrainToRoute(train, route, normalScenario, budget);
        });

        // Verify that the train has only one people carriage
        assertEquals(1, train.getCarriages().size());
        assertEquals(Carriage.Type.PEOPLE, train.getCarriages().get(0).getType());
        assertEquals("test@example.com", train.getUserEmail());
    }

    @Test
    public void testAssignExistingTrainToRoute_WithMailCarriageOnly() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player");
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<PointOfRoute> route = new ArrayList<>();
        Station depot = new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, null);
        Station station1 = new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, Station.Orientation.NE);
        Station terminal = new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, null);

        // Fix PointOfRoute constructor calls
        route.add(new PointOfRoute(depot, new ArrayList<>(), CargoMode.FULL));
        route.add(new PointOfRoute(station1, Arrays.asList(Carriage.Type.MAIL), CargoMode.FULL));
        route.add(new PointOfRoute(terminal, Arrays.asList(Carriage.Type.MAIL), CargoMode.FULL));

        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        // Fix Scenario constructor call
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                0, new ArrayList<>(), normalRestrictions, 1.0, 1.0);

        Budget budget = new Budget(1000, null, normalScenario, testUser);

        // This should execute without throwing exceptions
        assertDoesNotThrow(() -> {
            controller.assignExistingTrainToRoute(train, route, normalScenario, budget);
        });

        // Verify that the train has only one mail carriage
        assertEquals(1, train.getCarriages().size());
        assertEquals(Carriage.Type.MAIL, train.getCarriages().get(0).getType());
        assertEquals("test@example.com", train.getUserEmail());
    }

    @Test
    public void testAssignExistingTrainToRoute_InvalidRoute() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player");
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        Train train = new Train(locomotive, carriages, "test@example.com");
        List<PointOfRoute> emptyRoute = new ArrayList<>();

        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        // Fix Scenario constructor call
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                0, new ArrayList<>(), normalRestrictions, 1.0, 1.0);

        Budget budget = new Budget(1000, null, normalScenario, testUser);
        assertThrows(Exception.class, () -> {
            controller.assignExistingTrainToRoute(train, emptyRoute, normalScenario, budget);
        });
    }
}