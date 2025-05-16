package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents a train consisting of a single locomotive and one or more carriages.
 */
public class Train {
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private final String userEmail;

    /**
     * Creates a Train with the given locomotive and initial list of carriages.
     * @param locomotive the locomotive to attach
     * @param carriages one or more carriages
     * @throws IllegalArgumentException if the locomotive is null or there are no carriages provided
     */
    public Train(Locomotive locomotive, List<Carriage> carriages, String userEmail) {
        if (locomotive == null) {
            throw new IllegalArgumentException("Locomotive cannot be null");
        }
        if (carriages == null || carriages.isEmpty()) {
            throw new IllegalArgumentException("Train must have at least one carriage");
        }
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>(carriages);
        this.userEmail = userEmail;
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
