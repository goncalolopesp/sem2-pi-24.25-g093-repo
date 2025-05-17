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

        Map testMap = new Map("TestMap", 1000, 1000);
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("normal restriction");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);
        User testUser = new User("Test User", "test@example.com", "password", "player");
        Budget budget = new Budget(1000, testMap, normalScenario, null);
        budget.setUser(testUser);
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, 20, null));

        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);

        int moneyWon = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario, budget);

        assertEquals(132, moneyWon);
        assertEquals(1132, budget.getBudget());
        assertEquals(21, train.getCarriages().get(0).getCapacityOwned());
        assertEquals(9, train.getCarriages().get(1).getCapacityOwned());
        assertEquals(0, route.get(0).getCapacityOwned());
        assertEquals(0, route.get(1).getCapacityOwned());
        assertEquals(0, route.get(2).getCapacityOwned());
    }

    @Test
    public void testAssignTrainToRoute_VaccinationScenario() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        Map testMap = new Map("TestMap", 1000, 1000);
        List<String> vaccinationRestrictions = new ArrayList<>();
        vaccinationRestrictions.add("vaccination campaign");
        Scenario vaccinationScenario = new Scenario("Vaccination Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), vaccinationRestrictions);

        Budget budget = new Budget(1000, testMap, vaccinationScenario, null);
        User testUser = new User("Test User", "test@example.com", "password", "player");
        budget.setUser(testUser);
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "Vaccination Scenario", "test@example.com",
                100, 100, 0, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "Vaccination Scenario", "test@example.com",
                200, 200, 10, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Vaccination Scenario", "test@example.com",
                300, 300, 20, 20, 20, null));

        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE, Carriage.Type.MAIL);

        int moneyWon = controller.assignTrainToRoute(train, route, cargoTypes, vaccinationScenario, budget);

        assertEquals(237, moneyWon);
        assertEquals(1237, budget.getBudget());
        assertEquals(21, train.getCarriages().get(0).getCapacityOwned()); // People carriage
        assertEquals(9, train.getCarriages().get(1).getCapacityOwned()); // Mail carriage
        assertEquals(0, route.get(0).getCapacityOwned());
        assertEquals(0, route.get(1).getCapacityOwned());
        assertEquals(0, route.get(2).getCapacityOwned());
    }

    @Test
    public void testAssignTrainToRoute_OnlyPeopleCargo() {
        CurrentUser.getInstance().clear();
        AssignTrainController controller = new AssignTrainController();
        Map testMap = new Map("TestMap", 1000, 1000);
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("none");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);

        Budget budget = new Budget(1000, testMap, normalScenario, null);
        User testUser = new User("Test User", "test@example.com", "password", "player");
        budget.setUser(testUser);
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, 20, null));

        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.PEOPLE);

        int moneyWon = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario, budget);

        assertEquals(105, moneyWon);

        assertEquals(1105, budget.getBudget());

        assertEquals(21, train.getCarriages().get(0).getCapacityOwned());
        assertEquals(0, train.getCarriages().get(1).getCapacityOwned());

        assertEquals(0, route.get(0).getCapacityOwned());
        assertEquals(3, route.get(1).getCapacityOwned());
        assertEquals(6, route.get(2).getCapacityOwned());
    }

    @Test
    public void testAssignTrainToRoute_OnlyMailCargo() {
        CurrentUser.getInstance().clear();

        AssignTrainController controller = new AssignTrainController();

        Map testMap = new Map("TestMap", 1000, 1000);
        List<String> normalRestrictions = new ArrayList<>();
        normalRestrictions.add("none");
        Scenario normalScenario = new Scenario("Normal Scenario", "TestMap", new int[]{1900, 2000},
                new ArrayList<>(), normalRestrictions);

        Budget budget = new Budget(1000, testMap, normalScenario, null);
        User testUser = new User("Test User", "test@example.com", "password", "player");
        budget.setUser(testUser);
        CurrentUser.getInstance().setUser(testUser);

        Locomotive locomotive = new Locomotive("Test Locomotive", 500, "Steam", 100, 50, 1900, 2000, 120);
        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        Train train = new Train(locomotive, carriages, "test@example.com");

        List<Station> route = new ArrayList<>();
        route.add(new Station(Station.Type.DEPOT, "Depot", "TestMap", "Normal Scenario", "test@example.com",
                100, 100, 0, 0, 0, null));
        route.add(new Station(Station.Type.STATION, "Station1", "TestMap", "Normal Scenario", "test@example.com",
                200, 200, 10, 10, 10, Station.Orientation.NE));
        route.add(new Station(Station.Type.TERMINAL, "Terminal", "TestMap", "Normal Scenario", "test@example.com",
                300, 300, 20, 20, 20, null));

        List<Carriage.Type> cargoTypes = Arrays.asList(Carriage.Type.MAIL);

        int moneyWon = controller.assignTrainToRoute(train, route, cargoTypes, normalScenario, budget);

        assertEquals(27, moneyWon);

        assertEquals(1027, budget.getBudget());

        assertEquals(0, train.getCarriages().get(0).getCapacityOwned());
        assertEquals(9, train.getCarriages().get(1).getCapacityOwned());
        assertEquals(0, route.get(0).getCapacityOwned());
        assertEquals(7, route.get(1).getCapacityOwned());
        assertEquals(14, route.get(2).getCapacityOwned());
    }
}