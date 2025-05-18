package controller;

import domain.Building;
import domain.Station;
import repository.BuildingRepository;
import repository.Repositories;
import repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for retrieving station and building information
 * Provides methods to list stations and find buildings associated with specific stations
 */
public class ListStationController {

    /** Singleton instance of repositories */
    Repositories repositories = Repositories.getInstance();

    /** Repository for station data */
    StationRepository sr = repositories.getStationRepository();

    /** Repository for building data */
    BuildingRepository br = repositories.getBuildingRepository();

    /**
     * Retrieves all stations in the system
     * @return List of all stations
     */
    public List<Station> getStations(){
        return sr.getStations();
    }

    /**
     * Retrieves all buildings associated with a specific station
     * @param station The station to find buildings for
     * @return List of buildings located at the specified station
     */
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