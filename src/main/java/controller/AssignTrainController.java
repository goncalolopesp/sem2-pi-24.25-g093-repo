package controller;

import domain.*;
import repository.Repositories;
import repository.TrainRepository;
import repository.CarriageRepository;

import java.util.ArrayList;
import java.util.List;

public class AssignTrainController {

    // Values for calculating budget
    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;

    // Cargo distribution constants
    private static final double PEOPLE_RATIO = 0.7;  // 70% people (the rest is Mail)

    /**
     * Creates a train with the specified locomotive and cargo types, then assigns it to a route
     * @param locomotive The locomotive to use for the train
     * @param route The ordered list of stations the train will visit
     * @param cargoTypes The types of cargo this train will collect
     * @param peopleCarriageCount Number of people carriages to create
     * @param mailCarriageCount Number of mail carriages to create
     * @param scenario the scenario to check the scenario effects on the route
     * @param budget the player's budget
     * @param playerEmail the email of the player creating the train
     * @return Budget earned from this route
     */
    public int createTrainAndAssignToRoute(Locomotive locomotive, List<Station> route,
                                           List<Carriage.Type> cargoTypes, int peopleCarriageCount,
                                           int mailCarriageCount, Scenario scenario, Budget budget,
                                           String playerEmail) {

        // Create carriages based on the specified counts
        List<Carriage> carriages = new ArrayList<>();
        CarriageRepository carriageRepo = Repositories.getInstance().getCarriageRepository();

        // Create people carriages
        for (int i = 0; i < peopleCarriageCount; i++) {
            Carriage c = new Carriage(Carriage.Type.PEOPLE);
            carriageRepo.save(c);
            carriages.add(c);
        }

        // Create mail carriages
        for (int i = 0; i < mailCarriageCount; i++) {
            Carriage c = new Carriage(Carriage.Type.MAIL);
            carriageRepo.save(c);
            carriages.add(c);
        }

        // Create and save the train
        Train train = new Train(locomotive, carriages, playerEmail);
        TrainRepository trainRepo = Repositories.getInstance().getTrainRepository();
        trainRepo.save(train);

        System.out.println("Train created with " + carriages.size() + " carriages.");

        // Now assign the train to the route
        return assignTrainToRoute(train, route, cargoTypes, scenario, budget);
    }

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

        int moneyWon;
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
        System.out.println("Budget earned: " + moneyWon);
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