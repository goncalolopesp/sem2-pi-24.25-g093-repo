// dto/TrainDTO.java
package dto;

import java.util.List;

public class TrainDTO {
    private final String locomotiveModel;
    private final int totalCarriages;
    private final List<String> carriageTypes;
    private final List<Integer> carriageCounts;

    public TrainDTO(String locomotiveModel, int totalCarriages, List<String> carriageTypes, List<Integer> carriageCounts) {
        this.locomotiveModel = locomotiveModel;
        this.totalCarriages = totalCarriages;
        this.carriageTypes = carriageTypes;
        this.carriageCounts = carriageCounts;
    }

    public String getLocomotiveModel() {
        return locomotiveModel;
    }

    public int getTotalCarriages() {
        return totalCarriages;
    }

    public List<String> getCarriageTypes() {
        return carriageTypes;
    }

    public List<Integer> getCarriageCounts() {
        return carriageCounts;
    }
}
