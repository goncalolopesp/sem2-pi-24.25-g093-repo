package controller;

import domain.Budget;
import domain.CurrentUser;
import domain.Locomotive;
import domain.Scenario;
import repository.BudgetRepository;
import repository.LocomotiveRepository;
import repository.Repositories;

import java.util.*;

/**
 * Controller responsible for managing locomotive purchases and availability
 * Handles the creation, listing, and purchase of locomotives based on game scenarios
 */
public class BuyLocomotiveController {
    /** Repository for locomotive management */
    private final LocomotiveRepository locomotiveRepository;

    /** Current user session */
    CurrentUser session = CurrentUser.getInstance();

    /** Repository for budget management */
    private final BudgetRepository budgetRepository;

    /**
     * Constructor initializes repositories from the singleton repositories instance
     */
    public BuyLocomotiveController() {
        Repositories repositories = Repositories.getInstance();
        locomotiveRepository = repositories.getLocomotiveRepository();
        budgetRepository = repositories.getBudgetRepository();
    }

    /**
     * Creates a new locomotive in the system
     * @param modelName The model name of the locomotive
     * @param price The purchase price of the locomotive
     * @param type The type of locomotive (e.g., steam, diesel, electric)
     * @param fuelCost The fuel cost per operation
     * @param maintenanceCost The maintenance cost
     * @param startYearOfOperation The first year this locomotive is available
     * @param endYearOfOperation The last year this locomotive is available
     * @param topSpeed The maximum speed of the locomotive
     */
    public void createLocomotive(String modelName, int price, String type, int fuelCost, int maintenanceCost, int startYearOfOperation, int endYearOfOperation, int topSpeed) {
        locomotiveRepository.addLocomotive(modelName, price, type, fuelCost, maintenanceCost, startYearOfOperation, endYearOfOperation, topSpeed);
    }

    /**
     * Gets all locomotives available for a specific scenario based on time and technological restrictions
     * @param scenario The scenario to check locomotive availability against
     * @return List of locomotives available for the given scenario
     */
    public List<Locomotive> getLocomotives(Scenario scenario) {
        Set<Locomotive> locomotives = locomotiveRepository.getLocomotives();
        List<Locomotive> locomotivesList = new ArrayList<>();
        for (Locomotive locomotive : locomotives) {
            if(locomotive.getStartYearOfOperation() <= scenario.getTimeRestrictions()[0] && locomotive.getEndYearOfOperation() >= scenario.getTimeRestrictions()[0]) {
                for (String techRestriction : scenario.getTechnologicalRestrictions()){
                    if (techRestriction.equalsIgnoreCase(locomotive.getType())) {
                        locomotivesList.add(locomotive);
                    }
                }
            }
        }
        return locomotivesList;
    }

    /**
     * Gets all locomotives owned by the current player
     * @return List of locomotives owned by the player
     */
    public List<Locomotive> getPlayerLocomotives() {
        return session.getUser().getLocomotives();
    }

    /**
     * Checks if a locomotive can be purchased in the given scenario
     * @param modelName The model name of the locomotive to check
     * @param scenario The scenario to check against
     * @return true if the locomotive can be purchased, false otherwise
     */
    public boolean canBuyLocomotive(String modelName, Scenario scenario) {
        for (Locomotive locomotive : this.getLocomotives(scenario)){
            if (locomotive.getModelName().equalsIgnoreCase(modelName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Purchases a locomotive for the current player if available and affordable
     * @param modelName The model name of the locomotive to buy
     * @param scenario The current game scenario
     * @param map The current game map
     * @return true if purchase was successful, false otherwise
     */
    public boolean buyLocomotive(String modelName, Scenario scenario, domain.Map map) {
        if (canBuyLocomotive(modelName, scenario)) {
            Optional<Budget> budgetOpt = budgetRepository.getUserBudgetFromScenarioInMap(scenario, map, session.getUser());

            if (budgetOpt.isPresent()) {
                return locomotiveRepository.buyLocomotive(modelName, budgetOpt.get());
            } else {
                System.out.println("Error: No budget found for current user/scenario/map combination");
                return false;
            }
        }
        return false;
    }

    public boolean buyLocomotiveWithBudget(String modelName, Scenario scenario, Budget budget) {
        if (canBuyLocomotive(modelName, scenario)) {
            return locomotiveRepository.buyLocomotive(modelName, budget);
        }
        return false;
    }
}