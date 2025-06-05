package dto;

import domain.Carriage;
import domain.Train;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class provides a method for mapping a Train object to a TrainDTO object.
 * It extracts and summarizes specific information from the Train object, such as the
 * locomotive model, total number of carriages, and details about each type of carriage
 * present in the train.
 */
public class DTOMapperTrain {

    /**
     * Converts a Train object into its corresponding TrainDTO representation.
     * Extracts information such as the locomotive model, total number of carriages,
     * and a summary of carriage types along with their counts.
     *
     * @param train the Train object to be converted
     * @return a TrainDTO object containing summarized information about the train
     */
    public static TrainDTO toDTO(Train train) {
        List<String> types = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Carriage c : train.getCarriages()) {
            String type = c.getType().name();
            int index = types.indexOf(type);

            if (index == -1) {
                types.add(type);
                counts.add(1);
            } else {
                counts.set(index, counts.get(index) + 1);
            }
        }

        return new TrainDTO(
                train.getLocomotive().getModelName(),
                train.getCarriages().size(),
                types,
                counts
        );
    }
}
