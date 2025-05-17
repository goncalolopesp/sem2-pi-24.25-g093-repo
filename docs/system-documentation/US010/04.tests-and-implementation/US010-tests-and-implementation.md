# US010 - Assign a selected train to a route

## 4. Tests

**Test 1:** Tests assigning train to route in a normal scenario.

```java
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
```

**Test 2:** Tests assigning train to route in a vaccination scenario.

```java
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
```

**Test 3:** Tests assigning train to route with only people cargo.

```java
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
```

**Test 4:** Tests assigning train to route with only mail cargo.

```java
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
```

**Test 5:** Tests saving the train to repository.

```java
@Test
void saveShouldAddTrainToRepository(){
    Train result = repository.save(train1);
    assertEquals(train1, result);
    assertEquals(1, repository.getTrains().size());
    assertTrue(repository.getTrains().contains(train1));
}
```

**Test 6:** Tests duplicate verification in the repository.

```java
@Test
void saveShouldNotAddDuplicateTrain() {
    repository.save(train1);
    Train result = repository.save(train1);
    assertEquals(train1, result);
    assertEquals(1, repository.getTrains().size());
}
```

**Test 7:** Tests exception when train is null.
```java
@Test
void saveShouldThrowExceptionWhenTrainIsNull() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        repository.save(null);
    });
    assertEquals("Train cannot be null", exception.getMessage());
}
```

**Test 8:** Tests deleting a train from the repository.
```java
@Test
void deleteShouldRemoveTrainFromRepository() {
    repository.save(train1);
    boolean result = repository.delete(train1);
    assertTrue(result);
    assertEquals(0, repository.getTrains().size());
    assertFalse(repository.getTrains().contains(train1));
}
```

**Test 9:** Tests deleting a train that isn't in the repository.
```java
@Test
void deleteShouldReturnFalseWhenTrainNotFound() {
    boolean result = repository.delete(train1);
    assertFalse(result);
}
```

**Test 10:** Tests getting the list of trains from the repository.
```java
@Test
void getTrainsShouldReturnAllTrains() {
    repository.save(train1);
    repository.save(train2);
    List<Train> result = repository.getTrains();
    assertEquals(2, result.size());
    assertTrue(result.contains(train1));
    assertTrue(result.contains(train2));
}
```

**Test 11:** Tests if the list of trains is unmodifiable.
```java
@Test
@DisplayName("GetTrains should return unmodifiable list")
void getTrainsShouldReturnUnmodifiableList() {
    repository.save(train1);
    List<Train> result = repository.getTrains();
    assertThrows(UnsupportedOperationException.class, () -> {
        result.add(train2);
    });
}
```

**Test 12:** Tests if the list of trains is empty when none exist.
```java
@Test
void getTrainsShouldReturnEmptyListWhenNoTrains() {
    List<Train> result = repository.getTrains();
    assertTrue(result.isEmpty());
}
```

**Test 13:** Tests finding a specific train.
```java
@Test
void findTrainShouldReturnTrainWhenFound() {
    repository.save(train1);
    Optional<Train> result = repository.findTrain(train1);
    assertTrue(result.isPresent());
    assertEquals(train1, result.get());
}
```

**Test 14:** Tests finding a train that doesn't exist.
```java
@Test
void findTrainShouldReturnEmptyOptionalWhenTrainNotFound() {
    Optional<Train> result = repository.findTrain(train1);

    assertFalse(result.isPresent());
}
```

**Test 15:** Tests if repository correctly maintains multiple trains.
```java
@Test
void repositoryShouldMaintainMultipleTrainsCorrectly() {
    repository.save(train1);
    repository.save(train2);

    repository.delete(train1);

    assertEquals(1, repository.getTrains().size());
    assertFalse(repository.getTrains().contains(train1));
    assertTrue(repository.getTrains().contains(train2));

    Train train3 = new Train(
            new Locomotive("John Bull", 23, "Steam", 9, 7, 1831, 1853, 25),
            List.of(new Carriage(Carriage.Type.MAIL)),
            "user3@example.com"
    );
    repository.save(train3);

    assertEquals(2, repository.getTrains().size());
    assertTrue(repository.getTrains().contains(train2));
    assertTrue(repository.getTrains().contains(train3));
}
```

**Test 16:** Tests exception when locomotive is null.
```java
@Test
void constructorShouldThrowExceptionWhenLocomotiveIsNull() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        new Train(null, carriages, userEmail);
    });
    assertEquals("Locomotive cannot be null", exception.getMessage());
}
```

**Test 17:** Tests exception when carriages is null.
```java
@Test
    void constructorShouldThrowExceptionWhenCarriagesIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Train(locomotive, null, userEmail);
        });
        assertEquals("Train must have at least one carriage", exception.getMessage());
    }
```

**Test 18:** Tests exception when carriages is empty.
```java
@Test
    void constructorShouldThrowExceptionWhenCarriagesIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Train(locomotive, new ArrayList<>(), userEmail);
        });
        assertEquals("Train must have at least one carriage", exception.getMessage());
    }
```

**Test 19:** Tests getting the locomotive from a specific train.
```java
@Test
    void getLocomotiveShouldReturnCorrectLocomotive() {
        Locomotive result = train.getLocomotive();
        assertEquals(locomotive, result);
    }
```

**Test 20:** Tests if the carriage list is unmodifiable.
```java
@Test
    void getCarriagesShouldReturnUnmodifiableList() {
        List<Carriage> result = train.getCarriages();

        assertEquals(2, result.size());
        assertThrows(UnsupportedOperationException.class, () -> {
            result.add(new Carriage(Carriage.Type.PEOPLE));
        });
    }
```

**Test 21:** Tests getting the user email.
```java
@Test
    void getUserEmailShouldReturnCorrectEmail() {
        String result = train.getUserEmail();

        assertEquals(userEmail, result);
    }
```

**Test 22:** Tests adding carriage to train.
```java
@Test
    void addCarriageShouldAddNewCarriage() {
        Carriage newCarriage = new Carriage(Carriage.Type.PEOPLE);

        train.addCarriage(newCarriage);

        List<Carriage> carriageList = train.getCarriages();
        assertEquals(3, carriageList.size());
        assertTrue(carriageList.contains(newCarriage));
    }
```

**Test 23:** Tests adding a null carriage.
```java
@Test
    void addCarriageShouldThrowExceptionWhenCarriageIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            train.addCarriage(null);
        });
        assertEquals("Carriage cannot be null", exception.getMessage());
    }
```

**Test 24:** Tests removing a carriage from a train.
```java
@Test
    void removeCarriageShouldReturnTrueWhenCarriageExists() {
        Carriage existingCarriage = carriages.get(0);

        boolean result = train.removeCarriage(existingCarriage);

        assertTrue(result);
        assertEquals(1, train.getCarriages().size());
        assertFalse(train.getCarriages().contains(existingCarriage));
    }
```

**Test 25:** Tests removing a carriage that doesn't exist.
```java
@Test
    void removeCarriageShouldReturnFalseWhenCarriageDoesNotExist() {
        Carriage nonExistingCarriage = new Carriage(Carriage.Type.PEOPLE);
        nonExistingCarriage.setCapacityOwned(65);

        boolean result = train.removeCarriage(nonExistingCarriage);

        assertFalse(result);
        assertEquals(2, train.getCarriages().size());
    }
```

**Test 26:** Tests if train correctly handles different carriage types.
```java
@Test
    void trainShouldHandleDifferentCarriageTypesCorrectly() {
        Carriage peopleCarriage = new Carriage(Carriage.Type.PEOPLE);
        Carriage mailCarriage = new Carriage(Carriage.Type.MAIL);

        train.addCarriage(peopleCarriage);
        train.addCarriage(mailCarriage);

        List<Carriage> carriageList = train.getCarriages();
        assertEquals(4, carriageList.size());

        boolean hasPeopleCarriage = carriageList.stream()
                .anyMatch(c -> c.getType() == Carriage.Type.PEOPLE);
        boolean hasMailCarriage = carriageList.stream()
                .anyMatch(c -> c.getType() == Carriage.Type.MAIL);

        assertTrue(hasPeopleCarriage);
        assertTrue(hasMailCarriage);
    }
```

## 5. Construction (Implementation)

### Class AssignTrainController

```java
package controller;

import domain.*;

import java.util.List;

public class AssignTrainController {

    // Values for calculating budget
    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;

    // Cargo distribution constants
    private static final double PEOPLE_RATIO = 0.7;  // 70% people (the rest is Mail)

    /**
     * Assigns a train to a specific route with defined cargo types to collect
     * @param train The train to assign
     * @param route The ordered list of stations the train will visit
     * @param cargoTypes The types of cargo this train will collect
     * @param scenario the scenario to check the scenario effects on the route
     * @param budget the player's budget
     * @return Budget earned from this route
     */
    public int assignTrainToRoute(Train train, List<Station> route, List<Carriage.Type> cargoTypes, Scenario scenario, Budget budget) {
        int totalPeopleCollected = 0;
        int totalMailCollected = 0;

        // Initialize carriages - they should be empty at start
        List<Carriage> carriages = train.getCarriages();
        for (Carriage carriage : carriages) {
            carriage.setCapacityOwned(0);
        }

        boolean routeCompleted = false;

        // Visit each station in the route
        for (Station station : route) {
            if (routeCompleted) {
                break;
            }

            int stationCapacity = station.getCapacityOwned();
            if (stationCapacity <= 0) {
                continue; // Skip this station if no cargo available
            }

            // Calculate available cargo based on distribution (70% people, 30% mail)
            int availablePeople = (int) Math.round(stationCapacity * PEOPLE_RATIO);
            int availableMail = stationCapacity - availablePeople;

            // Collect people if requested
            if (cargoTypes.contains(Carriage.Type.PEOPLE)) {
                int peopleCollected = collectCargo(carriages, Carriage.Type.PEOPLE, availablePeople);
                totalPeopleCollected += peopleCollected;
                availablePeople -= peopleCollected;
            }

            // Collect mail if requested
            if (cargoTypes.contains(Carriage.Type.MAIL)) {
                int mailCollected = collectCargo(carriages, Carriage.Type.MAIL, availableMail);
                totalMailCollected += mailCollected;
                availableMail -= mailCollected;
            }

            // Update station's remaining capacity
            int remainingCapacity = availablePeople + availableMail;
            station.setCapacityOwned(remainingCapacity);

            // Check if all carriages are full
            boolean allCarriagesFull = true;
            for (Carriage carriage : carriages) {
                if (carriage.getCapacityOwned() < carriage.getCapacity()) {
                    allCarriagesFull = false;
                    break;
                }
            }

            if (allCarriagesFull) {
                routeCompleted = true;
                System.out.println("Route completed early: All carriages full!");
            }
        }

        boolean isVaccinationCampaign = scenario.getHistoricalRestrictions()
                .stream()
                .anyMatch(r -> r.equalsIgnoreCase("vaccination campaign"));

        int moneyWon = 0;
        if(isVaccinationCampaign){
            moneyWon = ((totalPeopleCollected * PEOPLE_VALUE) * 2) + (totalMailCollected * MAIL_VALUE);
        }

        else{
            moneyWon = (totalPeopleCollected * PEOPLE_VALUE) + (totalMailCollected * MAIL_VALUE);
        }


        // Print summary
        System.out.println("\n=== Train Route Summary ===");
        System.out.println("Total cargo collected: " + (totalPeopleCollected + totalMailCollected));
        System.out.println("People collected: " + totalPeopleCollected);
        System.out.println("Mail collected: " + totalMailCollected);
        System.out.println("Budget earned: " + budget.getBudget());
        System.out.println("=========================\n");

        budget.addBudget(moneyWon);
        return moneyWon;
    }

    /**
     * Collects cargo of a specific type and distributes it to available carriages
     * @param carriages List of all carriages
     * @param cargoType Type of cargo to collect
     * @param availableCargo Amount of cargo available at station
     * @return Amount of cargo collected
     */
    private int collectCargo(List<Carriage> carriages, Carriage.Type cargoType, int availableCargo) {
        int collected = 0;

        // Find carriages that match the cargo type
        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType && availableCargo > 0) {
                int spaceLeft = carriage.getCapacity() - carriage.getCapacityOwned();

                if (spaceLeft > 0) {
                    // Determine how much we can collect in this carriage
                    int toCollect = Math.min(availableCargo, spaceLeft);

                    // Update carriage and counters
                    carriage.setCapacityOwned(carriage.getCapacityOwned() + toCollect);
                    availableCargo -= toCollect;
                    collected += toCollect;
                }
            }
        }

        return collected;
    }
}
```

### Class TrainRepository

```java
package repository;

import domain.Train;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for storing and querying Trains instances.
 * <p>
 * Provides methods to add trains, delete trains, find a specific train and retrieve all trains.
 * </p>
 */
public class TrainRepository {
    private final List<Train> trains = new ArrayList<>();

    /**
     * Saves a train to the repository.
     * If the train already exists (by reference), it won't be added again.
     * @param train the train to save
     * @return the saved train
     */
    public Train save(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null");
        }
        if (!trains.contains(train)) {
            trains.add(train);
        }
        return train;
    }

    /**
     * Deletes a train from the repository.
     * @param train the train to delete
     * @return true if removed, false if not found
     */
    public boolean delete(Train train) {
        return trains.remove(train);
    }

    /**
     * Finds all trains in the repository.
     * @return unmodifiable list of trains
     */
    public List<Train> getTrains() {
        return Collections.unmodifiableList(trains);
    }

    /**
     * Finds a train by reference.
     * @param train the train to search for
     * @return Optional with the train if found
     */
    public Optional<Train> findTrain(Train train) {
        return trains.stream()
                .filter(t -> t.equals(train))
                .findFirst();
    }
}
```


