package dto;

import domain.City;
import domain.HouseBlock;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting domain objects to DTOs.
 */
public class DTOMapperCity {
    /**
     * Converts a City domain object to a CityDTO, encapsulating its relevant data.
     *
     * @param city the City object to convert
     * @return the corresponding CityDTO object containing the city's essential details
     */
    public static CityDTO toCityDTO(City city) {
        List<HouseBlockDTO> blockDTOs = city.getBlocks().stream()
                .map(DTOMapperCity::toHouseBlockDTO)
                .collect(Collectors.toList());

        return new CityDTO(
                city.getName(),
                city.getMapName(),
                city.getCenterX(),
                city.getCenterY(),
                blockDTOs
        );
    }

    /**
     * Converts a HouseBlock domain object to a HouseBlockDTO.
     *
     * @param block the HouseBlock object to be converted
     * @return a new HouseBlockDTO object containing the x and y coordinates of the given HouseBlock
     */
    public static HouseBlockDTO toHouseBlockDTO(HouseBlock block) {
        return new HouseBlockDTO(block.getX(), block.getY());
    }

    /**
     * Converts a list of HouseBlock domain objects into a list of HouseBlockDTO objects.
     *
     * @param blocks the list of HouseBlock objects to be converted
     * @return a list of corresponding HouseBlockDTO objects
     */
    public static List<HouseBlockDTO> toBlockDTOList(List<HouseBlock> blocks) {
        return blocks.stream()
                .map(DTOMapperCity::toHouseBlockDTO)
                .collect(Collectors.toList());
    }
}
