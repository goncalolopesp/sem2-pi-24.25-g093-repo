package dto;

import domain.City;
import domain.HouseBlock;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting domain objects to DTOs.
 */
public class DTOMapperCity {

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

    public static HouseBlockDTO toHouseBlockDTO(HouseBlock block) {
        return new HouseBlockDTO(block.getX(), block.getY());
    }

    public static List<HouseBlockDTO> toBlockDTOList(List<HouseBlock> blocks) {
        return blocks.stream()
                .map(DTOMapperCity::toHouseBlockDTO)
                .collect(Collectors.toList());
    }
}
