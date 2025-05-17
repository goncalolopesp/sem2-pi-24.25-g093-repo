package controller;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignTrainControllerTest {

    @Test
    public void testAssignTrainToRoute_NormalScenario() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player", new Budget(1000));
        CurrentUser.getInstance().setUser(testUser);
        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");
        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "test@example.com",
                100, 100, 100, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "test@example.com",
                200, 200, 200, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "test@example.com",
                300, 300, 300, 20, 20, null));
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);
        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);

        int budget = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario);
        assertEquals(440, budget);
        assertEquals(1440, testUser.getBudgetValue());
        assertEquals(70, train.getCarriages().get(0).getCapacityOwned()); // People carriage
        assertEquals(30, train.getCarriages().get(1).getCapacityOwned()); // Mail carriage
        assertEquals(0, route.get(0).getCapacityOwned());
        assertEquals(200, route.get(1).getCapacityOwned());
        assertEquals(300, route.get(2).getCapacityOwned());
    }

    @Test
    public void testAssignTrainToRoute_VaccinationScenario() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        User testUser = new User("Test User", "test@example.com", "password", "player", new Budget(1000));
        CurrentUser.getInstance().setUser(testUser);
        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");
        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "test@example.com",
                100, 100, 100, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "test@example.com",
                200, 200, 200, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "test@example.com",
                300, 300, 300, 20, 20, null));
        List<String> vaccinationRestrictions = new ArrayList<>();
        vaccinationRestrictions.add("vaccination campaign");
        Scenario vaccinationScenario = new Scenario("Vaccination Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), vaccinationRestrictions);
        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);

        int budget = controller.assignTrainToRoute(train, route, cargoTypes, vaccinationScenario);

        // Validate results with doubled people value
        // Expected people: 70 (capacity)
        // Expected mail: 30 (capacity)
        // People value: 5 * 2 = 10 (doubled due to vaccination campaign), Mail value: 3
        // Expected budget: (70 * 10) + (30 * 3) = 700 + 90 = 790
        assertEquals(790, budget);

        // Check if the user's budget was updated
        assertEquals(1790, testUser.getBudgetValue());
    }

    @Test
    public void testAssignTrainToRoute_OnlyPeopleCargo() {
        // Ensure we have a clean CurrentUser
        CurrentUser.getInstance().clear();

        // Initialize the controller
        AssignTrainController controller = new AssignTrainController();

        // Create a test user with initial budget
        User testUser = new User("Test User", "test@example.com", "password", "player", new Budget(1000));
        CurrentUser.getInstance().setUser(testUser);

        // Create train components
        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        // Create route
        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "test@example.com",
                100, 100, 100, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "test@example.com",
                200, 200, 200, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "test@example.com",
                300, 300, 300, 20, 20, null));

        // Create normal scenario
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);

        // Test collecting only people
        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE);

        int budget = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario);

        // Validate results
        // Expected people: 70 (capacity), Expected mail: 0 (not collecting)
        // People value: 5, Mail value: 3
        // Expected budget: (70 * 5) + (0 * 3) = 350
        assertEquals(350, budget);

        // Check if the user's budget was updated
        assertEquals(1350, testUser.getBudgetValue());

        // Check if only people carriage was filled
        assertEquals(70, train.getCarriages().get(0).getCapacityOwned()); // People carriage
        assertEquals(0, train.getCarriages().get(1).getCapacityOwned());  // Mail carriage not used
    }

    @Test
    public void testAssignTrainToRoute_OnlyMailCargo() {
        // Ensure we have a clean CurrentUser
        CurrentUser.getInstance().clear();

        // Initialize the controller
        AssignTrainController controller = new AssignTrainController();

        // Create a test user with initial budget
        User testUser = new User("Test User", "test@example.com", "password", "player", new Budget(1000));
        CurrentUser.getInstance().setUser(testUser);

        // Create train components
        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        // Create route
        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "test@example.com",
                100, 100, 100, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "test@example.com",
                200, 200, 200, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "test@example.com",
                300, 300, 300, 20, 20, null));

        // Create normal scenario
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);
        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.MAIL);

        int budget = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario);
        assertEquals(90, budget);
        assertEquals(1090, testUser.getBudgetValue());
        assertEquals(0, train.getCarriages().get(0).getCapacityOwned());
        assertEquals(30, train.getCarriages().get(1).getCapacityOwned());
    }
}