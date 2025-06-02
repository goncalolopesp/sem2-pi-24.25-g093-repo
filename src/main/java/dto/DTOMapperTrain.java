// dto/TrainMapper.java
package dto;

import domain.Carriage;
import domain.Train;

import java.util.ArrayList;
import java.util.List;

public class DTOMapperTrain {

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
