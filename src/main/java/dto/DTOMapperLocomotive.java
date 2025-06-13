package dto;

import domain.Locomotive;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Locomotive domain objects and LocomotiveDTO objects.
 * Provides static methods for transforming single objects and collections.
 */
public class DTOMapperLocomotive {

    /**
     * Converts a Locomotive object into its corresponding LocomotiveDTO representation.
     * Extracts essential information such as model name, price, type, top speed, and availability.
     *
     * @param locomotive the Locomotive object to be converted
     * @return a LocomotiveDTO object containing summarized information about the locomotive
     */
    public static LocomotiveDTO toDTO(Locomotive locomotive) {
        return new LocomotiveDTO(
                locomotive.getModelName(),
                locomotive.getPrice(),
                locomotive.getType(),
                locomotive.getTopSpeed(),
                locomotive.getAvailability()
        );
    }

    /**
     * Converts a list of Locomotive objects into their corresponding LocomotiveDTO representations.
     *
     * @param locomotives the list of Locomotive objects to be converted
     * @return a list of LocomotiveDTO objects
     */
    public static List<LocomotiveDTO> toDTOList(List<Locomotive> locomotives) {
        return locomotives.stream()
                .map(DTOMapperLocomotive::toDTO)
                .collect(Collectors.toList());
    }
}