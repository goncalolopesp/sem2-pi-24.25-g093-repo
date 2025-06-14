package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents a train consisting of a single locomotive and one or more carriages.
 */
public class Train implements Serializable {
    private final Locomotive locomotive;
    private final List<Carriage> carriages;
    private final String userEmail;
    private final Scenario scenario;

    /**
     * Creates a Train with the given locomotive and initial list of carriages.
     * @param locomotive the locomotive to attach
     * @param carriages one or more carriages
     * @throws IllegalArgumentException if the locomotive is null or there are no carriages provided
     */
    public Train(Locomotive locomotive, List<Carriage> carriages, String userEmail,Scenario scenario) {
        if (locomotive == null) {
            throw new IllegalArgumentException("Locomotive cannot be null");
        }
        if (carriages == null || carriages.isEmpty()) {
            throw new IllegalArgumentException("Train must have at least one carriage");
        }
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>(carriages);
        this.userEmail = userEmail;
        this.scenario = scenario;
    }

    /**
     * Method that return the locomotive attached to this train
     * @return the locomotive attached to this train
     */
    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Method that returns the list of all carriages connected to the train
     * @return the list of all carriages connected to the train
     */
    public List<Carriage> getCarriages() {
        return Collections.unmodifiableList(carriages);
    }

    /**
     * Method that returns the email of the player who owns the train
     * @return the email of the player who owns the train
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Retrieves the scenario associated with the train.
     *
     * @return the scenario linked to this train
     */
    public Scenario getScenario() {return scenario;}

    /**
     * Method that attaches a carraige to the train
     * @param carriage the carriage to attach
     */
    public void addCarriage(Carriage carriage) {
        if (carriage == null) {
            throw new IllegalArgumentException("Carriage cannot be null");
        }
        carriages.add(carriage);
    }

    /**
     * Removes a carriage from the train.
     * @param carriage the carriage to remove
     * @return true if the carriage was removed; false if the carriage was not found
     */
    public boolean removeCarriage(Carriage carriage) {
        return carriages.remove(carriage);
    }
}
