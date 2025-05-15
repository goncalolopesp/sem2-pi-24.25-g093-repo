package controller;

import domain.Building;
import domain.Station;
import repository.BuildingRepository;
import repository.Repositories;
import repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

public class ListStationController {

    Repositories repositories = Repositories.getInstance();
    StationRepository sr = repositories.getStationRepository();
    BuildingRepository br = repositories.getBuildingRepository();

    public List<Station> getStations(){
        return sr.getStations();
    }

    public List<Building> getBuildingByStation(Station station){
        List<Building> lstBuilding = new ArrayList<>();
        List<Building> fetched = br.getBuildings();

        for (Building building : fetched) {
            if (building.getStation().equals(station)){
                lstBuilding.add(building);
            }
        }
        return lstBuilding;
    }

}
