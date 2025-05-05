package repository;

import domain.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapRepository {
    private final List<Map> maps;
    public MapRepository() { maps = new ArrayList<>(); }

    public void add(Map map) {
        if (validateMap(map)) {
            maps.add(map);
        }
    }

    public Optional<Map> getMap(String name) {
        return maps.stream().filter(m -> m.getMapName().equals(name)).findFirst();
    }

    private boolean validateMap(Map map) {
        return !maps.contains(map);
    }

    public boolean checkNameUniqueness(String name) {
        return maps.stream().noneMatch(m -> m.getMapName().equals(name));
    }

    public List<Map> getMaps() {
        //This is a defensive copy, so that the repository cannot be modified from the outside.
        return List.copyOf(maps);
    }
}