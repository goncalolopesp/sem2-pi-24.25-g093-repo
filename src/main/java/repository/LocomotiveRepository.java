package repository;

import java.util.*;

import domain.Budget;
import domain.CurrentUser;
import domain.Locomotive;
import domain.User;

/**
 * Repository class responsible for managing locomotive data and operations.
 * This class handles the storage, retrieval, and purchase of locomotive objects
 * within the application.
 */
public class LocomotiveRepository {

    /** Collection of all available locomotives in the system */
    Set<Locomotive> locomotives = new HashSet<>();

    /** Current user session for user-specific operations */
    CurrentUser session = CurrentUser.getInstance();

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
    public boolean buyLocomotive(Locomotive locomotive, Budget money) {
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
        return true;
    }

    /**
     * Overloaded method to purchase a locomotive by its model name.
     * Finds the locomotive in the repository and attempts to purchase it.
     *
     * @param modelName The model name of the locomotive to purchase
     * @param money The budget to use for the purchase
     * @return true if the purchase was successful, false if the locomotive wasn't found or if insufficient funds
     */
    public boolean buyLocomotive(String modelName, Budget money){
        Optional<Locomotive> locomotive = getLocomotive(modelName);
        return locomotive.filter(value -> buyLocomotive(value, money)).isPresent();
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
}