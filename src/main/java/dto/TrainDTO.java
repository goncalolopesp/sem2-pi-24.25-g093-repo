package dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a train.
 * This class encapsulates information about a train, including the locomotive model,
 * the total number of carriages, and details about the types and counts of carriages.
 * It is used to transfer this summarized data between different layers of the application.
 */
public class TrainDTO {
    private final String locomotiveModel;
    private final int totalCarriages;
    private final List<String> carriageTypes;
    private final List<Integer> carriageCounts;

    /**
     * Constructs a new instance of TrainDTO.
     *
     * @param locomotiveModel the model of the locomotive used in the train
     * @param totalCarriages the total number of carriages in the train
     * @param carriageTypes a list of carriage types present in the train
     * @param carriageCounts a list of counts corresponding to each carriage type
     */
    public TrainDTO(String locomotiveModel, int totalCarriages, List<String> carriageTypes, List<Integer> carriageCounts) {
        this.locomotiveModel = locomotiveModel;
        this.totalCarriages = totalCarriages;
        this.carriageTypes = carriageTypes;
        this.carriageCounts = carriageCounts;
    }

    /**
     * Retrieves the model name of the locomotive.
     *
     * @return the model name of the locomotive as a String
     */
    public String getLocomotiveModel() {
        return locomotiveModel;
    }

    /**
     * Retrieves the total number of carriages in the train.
     *
     * @return the total number of carriages
     */
    public int getTotalCarriages() {
        return totalCarriages;
    }

    /**
     * Retrieves the list of carriage types associated*/
    public List<String> getCarriageTypes() {
        return carriageTypes;
    }

    /**
     * Retrieves the counts of carriages in the train.
     * The counts correspond to the respective carriage types,
     * providing an overview of how many carriages exist for each type.
     *
     * @return a list of integers representing the counts of carriages for each carriage type
     */
    public List<Integer> getCarriageCounts() {
        return carriageCounts;
    }
}
