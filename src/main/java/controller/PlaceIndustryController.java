package controller;


import domain.Industry;

import domain.Map;
import repository.IndustryRepository;
import repository.MapRepository;


import java.util.List;

public class PlaceIndustryController {

    private  IndustryRepository instanceIndustryRepository;
    private MapRepository instanceMapRepository;

    public PlaceIndustryController() {
        this.instanceIndustryRepository = repository.Repositories.getInstance().getIndustryRepository();
        this.instanceMapRepository = repository.Repositories.getInstance().getMapRepository();
    }

    public PlaceIndustryController(IndustryRepository instanceIndustryRepository, MapRepository instanceMapRepository) {
        this.instanceIndustryRepository = instanceIndustryRepository;
        this.instanceMapRepository = instanceMapRepository;
    }

    public List<Map> getMaps() {
        return instanceMapRepository.getMaps();
    }
    public List<Industry> getIndustries() {
        return instanceIndustryRepository.getIndustries();
    }

    public boolean placeIndustryOnMap(Industry industry, int positionX,int positionY, Map map) {
        return map.placeIndustry(industry, positionX, positionY);
    }
}
