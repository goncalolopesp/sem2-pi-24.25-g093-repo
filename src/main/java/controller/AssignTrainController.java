package controller;

import domain.Carriage;
import domain.Station;
import domain.Train;

import java.util.List;

public class AssignTrainController {

    // Values for calculating budget
    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;

    // Cargo distribution constants
    private static final double PEOPLE_RATIO = 0.7;  // 70% people
    private static final double MAIL_RATIO = 0.3;    // 30% mail

    /**
     * Assigns a train to a specific route with defined cargo types to collect
     * @param train The train to assign
     * @param route The ordered list of stations the train will visit
     * @param cargoTypes The types of cargo this train will collect
     * @return Budget earned from this route
     */
    public int assignTrainToRoute(Train train, List<Station> route, List<Carriage.Type> cargoTypes) {
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

        // Calculate total budget earned
        int budget = (totalPeopleCollected * PEOPLE_VALUE) + (totalMailCollected * MAIL_VALUE);

        // Print summary
        System.out.println("\n=== Train Route Summary ===");
        System.out.println("Total cargo collected: " + (totalPeopleCollected + totalMailCollected));
        System.out.println("People collected: " + totalPeopleCollected);
        System.out.println("Mail collected: " + totalMailCollected);
        System.out.println("Budget earned: " + budget);
        System.out.println("=========================\n");

        return budget;
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