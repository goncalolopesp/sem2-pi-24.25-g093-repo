package repository;

import java.io.Serializable;
import java.util.*;

import domain.*;

/**
 * Repository class responsible for managing locomotive data and operations.
 * This class handles the storage, retrieval, and purchase of locomotive objects
 * within the application.
 */
public class LocomotiveRepository implements Serializable {

    /** Collection of all available locomotives in the system */
    Set<Locomotive> locomotives = new HashSet<>();

    /** Current user session for user-specific operations */
    CurrentUser session = CurrentUser.getInstance();

    /**
     * A transient field that holds a reference to a {@code TrainRepository} instance.
     * Used for managing train-related storage and query operations.
     *
     * This field is marked as transient to avoid serialization, as the {@code TrainRepository}
     * may involve runtime state or dependencies that are non-serializable.
     * The {@code TrainRepository} provides functionalities such as saving, updating, deleting,
     * and retrieving train objects.
     */
    private transient TrainRepository trainRepository;

    private TrainRepository getTrainRepository() {
        if (trainRepository == null) {
            trainRepository = Repositories.getInstance().getTrainRepository();
        }
        return trainRepository;
    }

    /**
     * Adds a locomotive to the repository if it doesn't already exist.
     *
     * @param locomotive The locomotive object to add
     * @return true if the locomotive was added successfully, false if it already exists
     */
    public boolean addLocomotive(Locomotive locomotive) {
        if (!checkIfLocomotiveExists(locomotive)) {
            locomotives.add(locomotive);
            return true;
        }
        return false;
    }

    /**
     * Creates and adds a new locomotive to the repository based on provided parameters.
     *
     * @param modelName Name/model of the locomotive
     * @param price Purchase price of the locomotive
     * @param type Type/category of the locomotive
     * @param fuelCost Cost of fuel for operating the locomotive
     * @param maintenanceCost Cost of maintaining the locomotive
     * @param startYearOfOperation Year when the locomotive started/starts operation
     * @param endYearOfOperation Year when the locomotive ended/ends operation
     * @param topSpeed Maximum speed of the locomotive
     * @return true if the locomotive was added successfully, false if a locomotive with the same model name already exists
     */
    public boolean addLocomotive(String modelName, int price, String type, int fuelCost, int maintenanceCost,
                                 int startYearOfOperation, int endYearOfOperation, int topSpeed) {
        Locomotive l1 = new Locomotive(modelName, price, type, fuelCost, maintenanceCost,
                startYearOfOperation, endYearOfOperation, topSpeed);
        if (!checkIfLocomotiveExists(l1)){
            locomotives.add(l1);
            return true;
        }
        return false;
    }

    /**
     * Checks if a locomotive with the same model name already exists in the repository.
     *
     * @param locomotive The locomotive to check for existence
     * @return true if a locomotive with the same model name exists, false otherwise
     */
    public boolean checkIfLocomotiveExists(Locomotive locomotive) {
        for (Locomotive b : locomotives) {
            if (b.getModelName().equalsIgnoreCase(locomotive.getModelName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the purchase of a locomotive for the current user.
     * Checks if the user has sufficient budget and adds the locomotive to the user's collection.
     *
     * @param locomotive The locomotive to purchase
     * @param money The budget to use for the purchase
     * @return true if the purchase was successful, false if insufficient funds
     */
    public boolean buyLocomotive(Locomotive locomotive, Budget money, Scenario scenario) {
        User user = session.getUser();
        // Check if user has enough money to buy the locomotive
        if (money.getBudget() < locomotive.getPrice()) {
            return false;
        }
        // Add locomotive to user's collection
        List<Locomotive> boughtLocomotives = user.getLocomotives();
        boughtLocomotives.add(locomotive);
        user.setLocomotives(boughtLocomotives);
        // Deduct the price from the budget
        money.subtractBudget(locomotive.getPrice());

        List<Carriage> carriages = new ArrayList<>();
        carriages.add(new Carriage(Carriage.Type.PEOPLE));
        carriages.add(new Carriage(Carriage.Type.MAIL));
        carriages.add(new Carriage(Carriage.Type.COAL));
        carriages.add(new Carriage(Carriage.Type.IRON));
        carriages.add(new Carriage(Carriage.Type.STEEL));
        Train train1 = new Train(locomotive,carriages,user.getEmail(),scenario);

        List<Carriage> carriages2 = new ArrayList<>();
        carriages2.add(new Carriage(Carriage.Type.PEOPLE));
        carriages2.add(new Carriage(Carriage.Type.MAIL));
        carriages2.add(new Carriage(Carriage.Type.COAL));
        carriages2.add(new Carriage(Carriage.Type.IRON));
        carriages2.add(new Carriage(Carriage.Type.STEEL));
        carriages2.add(new Carriage(Carriage.Type.PEOPLE));
        carriages2.add(new Carriage(Carriage.Type.MAIL));
        carriages2.add(new Carriage(Carriage.Type.COAL));
        carriages2.add(new Carriage(Carriage.Type.IRON));
        carriages2.add(new Carriage(Carriage.Type.STEEL));

        Train train2 = new Train(locomotive,carriages2,user.getEmail(),scenario);

        getTrainRepository().save(train1);
        getTrainRepository().save(train2);
        return true;
    }

    /**
     * Attempts to purchase a locomotive with the given model name using the provided budget and scenario.
     * Retrieves the locomotive by its model name, checks if the purchase is valid, and completes
     * the transaction if possible.
     *
     * @param modelName The name or model of the locomotive to purchase
     * @param money The budget available for the purchase
     * @param scenario The current scenario in which the purchase is being made
     * @return true if the locomotive was successfully purchased, false otherwise
     */
    public boolean buyLocomotive(String modelName, Budget money, Scenario scenario) {
        Optional<Locomotive> locomotive = getLocomotive(modelName);
        return locomotive.filter(value -> buyLocomotive(value, money, scenario)).isPresent();
    }

    /**
     * Removes a locomotive from the repository if it exists.
     *
     * @param locomotive The locomotive to remove
     * @return true if the locomotive was successfully removed, false if it didn't exist
     */
    public boolean deleteLocomotive(Locomotive locomotive){
        if (checkIfLocomotiveExists(locomotive)) {
            locomotives.remove(locomotive);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a locomotive by its model name.
     *
     * @param modelName The model name of the locomotive to find
     * @return An Optional containing the locomotive if found, or an empty Optional if not found
     */
    public Optional<Locomotive> getLocomotive(String modelName){
        for (Locomotive l : locomotives) {
            if (l.getModelName().equalsIgnoreCase(modelName)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets all locomotives in the repository.
     *
     * @return The set of all locomotives
     */
    public Set<Locomotive> getLocomotives() {
        return locomotives;
    }

    /**
     * Copies all locomotives from another repository into the current repository.
     * Clears the current repository's locomotives before copying.
     *
     * @param other The source repository from which to copy locomotives.
     */
    public void copyFrom(LocomotiveRepository other) {
        this.locomotives.clear();
        this.locomotives.addAll(other.getLocomotives());
    }
}