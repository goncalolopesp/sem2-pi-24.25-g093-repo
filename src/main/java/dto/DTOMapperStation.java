package dto;

import domain.Station;
import domain.Building;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Station/Building domain objects and their DTO representations.
 * Provides static methods for transforming single objects and collections.
 */
public class DTOMapperStation {

    /**
     * Converts a Station object into its corresponding StationDTO representation.
     *
     * @param station the Station object to be converted
     * @return a StationDTO object containing basic station information
     */
    public static StationDTO toDTO(Station station) {
        return StationDTO.fromEntity(station);
    }

    /**
     * Converts a list of Station objects into their corresponding StationDTO representations.
     *
     * @param stations the list of Station objects to be converted
     * @return a list of StationDTO objects
     */
    public static List<StationDTO> toDTOList(List<Station> stations) {
        return stations.stream()
                .map(DTOMapperStation::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Station object and its associated buildings into a StationDetailDTO representation.
     *
     * @param station the Station object to be converted
     * @param buildings the list of Building objects associated with the station
     * @return a StationDetailDTO object containing complete station and building information
     */
    public static StationDetailDTO toDetailDTO(Station station, List<Building> buildings) {
        return StationDetailDTO.fromEntity(station, buildings);
    }

    /**
     * Converts a list of Building objects into their corresponding BuildingDTO representations.
     *
     * @param buildings the list of Building objects to be converted
     * @return a list of BuildingDTO objects
     */
    public static List<BuildingDTO> buildingsToDTOList(List<Building> buildings) {
        return buildings.stream()
                .map(BuildingDTO::fromEntity)
                .collect(Collectors.toList());
    }
}