package dto;

import domain.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that provides methods to map between Map domain objects and MapDTO objects.
 *
 * This class includes static methods for converting Map to MapDTO, MapDTO to Map,
 * and their respective list conversions.
 */
public class MapMapper {

    /**
     * Converts a Map domain object into a MapDTO object.
     *
     * @param map The Map domain object to be converted. If null, this method will return null.
     * @return A MapDTO object representing the given Map, or null if the input map is null.
     */
    public static MapDTO toDTO(Map map) {
        if (map == null) {
            return null;
        }
        return new MapDTO(map.getMapName());
    }

    /**
     * Converts a list of Map domain objects into a list of MapDTO objects.
     *
     * @param maps The list of Map domain objects to be converted. If null, an empty list is returned.
     * @return A list of MapDTO objects corresponding to the input list of Map objects.
     *         If the input list is null or contains null elements, they are handled accordingly.
     */
    public static List<MapDTO> toDTO(List<Map> maps){
        if (maps == null) {
            return new ArrayList<>();
        }

        List<MapDTO> mapDTOs = new ArrayList<>();
        for (Map map : maps) {
            MapDTO mapDTO = toDTO(map);
            if (mapDTO != null) {
                mapDTOs.add(mapDTO);
            }
        }
        return mapDTOs;
    }
}
