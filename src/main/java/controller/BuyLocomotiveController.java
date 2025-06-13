package controller;

import domain.Budget;
import domain.CurrentUser;
import domain.Locomotive;
import domain.Map;
import domain.Scenario;
import dto.LocomotiveDTO;
import dto.DTOMapperLocomotive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import repository.BudgetRepository;
import repository.LocomotiveRepository;
import repository.Repositories;

import java.io.IOException;
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

    private PlayerMenuController playerMenuController;

    private Scenario currentScenario;

    private Budget playerBudget;

    private Stage currentStage;

    @FXML
    private ComboBox<Locomotive> locomotiveList_cmb;

    @FXML
    private TextArea locomotiveDetails_TxtA;

    @FXML
    private AnchorPane BuyLocomotive;

    @FXML
    public Label budgetLabel;

    @FXML
    public TextArea infoLocomotive;

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
     * @return List of LocomotiveDTO objects available for the given scenario
     */
    public List<LocomotiveDTO> getLocomotives(Scenario scenario) {
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
        return DTOMapperLocomotive.toDTOList(locomotivesList);
    }

    /**
     * Gets the original locomotive objects for internal operations
     * @param scenario The scenario to check locomotive availability against
     * @return List of Locomotive domain objects available for the given scenario
     */
    private List<Locomotive> getLocomotiveEntities(Scenario scenario) {
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
     * Checks if a locomotive can be purchased in the given scenario
     * @param modelName The model name of the locomotive to check
     * @param scenario The scenario to check against
     * @return true if the locomotive can be purchased, false otherwise
     */
    public boolean canBuyLocomotive(String modelName, Scenario scenario) {
        for (Locomotive locomotive : this.getLocomotiveEntities(scenario)){
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
     * @return true if purchase was successful, false otherwise
     */
    public boolean buyLocomotive(String modelName, Scenario scenario) {
        if (canBuyLocomotive(modelName, scenario)) {
            return locomotiveRepository.buyLocomotive(modelName, playerBudget);
        }
        return false;
    }

    /**
     * Purchases a locomotive using DTO for the current player if available and affordable
     * @param locomotiveDTO The LocomotiveDTO containing locomotive information
     * @param scenario The current game scenario
     * @param budget The budget to use for the purchase
     * @return true if purchase was successful, false otherwise
     */
    public boolean buyLocomotiveDTO(LocomotiveDTO locomotiveDTO, Scenario scenario, Budget budget) {
        if (canBuyLocomotive(locomotiveDTO.getModelName(), scenario)) {
            return locomotiveRepository.buyLocomotive(locomotiveDTO.getModelName(), budget);
        }
        return false;
    }

    public boolean buyLocomotiveWithBudget(String modelName, Scenario scenario, Budget budget) {
        if (canBuyLocomotive(modelName, scenario)) {
            return locomotiveRepository.buyLocomotive(modelName, budget);
        }
        return false;
    }

    @FXML
    public void buySelectedLocomotive(ActionEvent event) {
        Locomotive selectedLocomotive = locomotiveList_cmb.getSelectionModel().getSelectedItem();
        if (canBuyLocomotive(selectedLocomotive.getModelName(), currentScenario)) {
            buyLocomotive(selectedLocomotive.getModelName(), currentScenario);
            if (currentStage != null) {
                currentStage.close();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Not Enough Budget");
            alert.show();
        }
    }

    @FXML
    public void returnToMenu(ActionEvent event) throws IOException {
        if (currentStage != null) {
            currentStage.close();
        }
    }

    public void addLocomotivesToComboBox(){
        // Add null check as safety measure
        if (currentScenario == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Scenario is null");
            alert.show();
            return;
        }

        // Clear existing items first
        locomotiveList_cmb.getItems().clear();

        for (Locomotive locomotive : getLocomotiveEntities(currentScenario)){
            locomotiveList_cmb.getItems().add(locomotive);
        }
    }

    public void addOwnedLocomotivesToComboBox(){
        // Add null check as safety measure
        if (currentScenario == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Scenario is null");
            alert.show();
            return;
        }

        // Clear existing items first
        locomotiveList_cmb.getItems().clear();

        for (Locomotive locomotive : session.getUser().getLocomotives()){
            if (locomotiveRepository.getLocomotives().contains(locomotive)){
                locomotiveList_cmb.getItems().add(locomotive);
            }
        }
    }

    @FXML
    public void listLocomotiveDetails(ActionEvent event) {
        Locomotive selectedLocomotive = locomotiveList_cmb.getSelectionModel().getSelectedItem();
        String txt = "Name: " + selectedLocomotive.getModelName() + "\n" +
                "topSpeed: " + selectedLocomotive.getTopSpeed() + "\n" +
                "Type: " + selectedLocomotive.getType() + "\n" +
                "Maintenance Cost: " + selectedLocomotive.getMaintenanceCost() + "\n" +
                "Fuel Cost: " + selectedLocomotive.getFuelCost() + "\n" +
                "Price: " + selectedLocomotive.getPrice() + "\n";
        locomotiveDetails_TxtA.setText(txt);
    }

    public void initData(Scenario scenario, Budget budget, Stage stage, int app) {
        this.currentScenario = scenario;
        this.playerBudget = budget;
        this.currentStage = stage;

        if (app == 1) {
            updateBudgetLabel();
            // Now that we have the scenario, we can safely load the locomotives
            addLocomotivesToComboBox();
        }else{
            addOwnedLocomotivesToComboBox();
        }
    }

    private void updateBudgetLabel() {
        if (playerBudget != null) {
            budgetLabel.setText("Current Budget: $" + playerBudget.getBudget());
        }
    }

    @FXML
    public void LocomotiveList_IndexChanged(){
        Locomotive selectedLocomotive = locomotiveList_cmb.getSelectionModel().getSelectedItem();
        String txt = "Name: " + selectedLocomotive.getModelName() + "\n" +
                "topSpeed: " + selectedLocomotive.getTopSpeed() + "\n" +
                "Type: " + selectedLocomotive.getType() + "\n" +
                "Maintenance Cost: " + selectedLocomotive.getMaintenanceCost() + "\n" +
                "Fuel Cost: " + selectedLocomotive.getFuelCost() + "\n" +
                "Price: " + selectedLocomotive.getPrice() + "\n";
        infoLocomotive.setText(txt);
    }

}
