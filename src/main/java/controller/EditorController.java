package controller;

import dto.MapDTO;
import dto.MapMapper;
import repository.MapRepository;
import repository.Repositories;

import java.util.List;

public class EditorController {

    MapRepository mapRepository = Repositories.getInstance().getMapRepository();

    public List<MapDTO> getMaps() {
        return MapMapper.toDTO(mapRepository.getMaps());
    }
}
