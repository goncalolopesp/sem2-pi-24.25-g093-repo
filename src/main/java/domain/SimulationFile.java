package domain;

import repository.BuildingRepository;
import repository.Repositories;
import repository.SimulationRepository;
import repository.StationRepository;

import java.io.*;

public class SimulationFile {
    public static final String FILE_NAME = "simulation.sim";

    /**
     * Loads simulation data from the default simulation file.
     *
     * @param simRepo the simulation repository into which the loaded simulation data will be copied
     * @param stationRepo the station repository into which the loaded station data will be copied
     * @param buildingRepo the building repository into which the loaded building data will be copied
     * @return true if the data was successfully loaded or no save file exists; false if an error occurred during loading
     */
    public boolean load(SimulationRepository simRepo, StationRepository stationRepo, BuildingRepository buildingRepo) {
        return load(FILE_NAME, simRepo, stationRepo, buildingRepo);
    }

    public boolean load(String fileName, SimulationRepository simRepo, StationRepository stationRepo, BuildingRepository buildingRepo) {
        return load(new File(fileName), simRepo, stationRepo, buildingRepo);
    }

    public boolean load(File file, SimulationRepository simRepo, StationRepository stationRepo, BuildingRepository buildingRepo) {
        if (!file.exists()) {
            return true;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            SimulationRepository loadedRepo = (SimulationRepository) in.readObject();
            StationRepository loadedRepo2 = (StationRepository) in.readObject();
            BuildingRepository loadedRepo3 = (BuildingRepository) in.readObject();

            // Instead of setting on singleton, copy data to the target repo
            // You'll need to add a method to copy simulations
            simRepo.copyFrom(loadedRepo);
            stationRepo.copyFrom(loadedRepo2);
            buildingRepo.copyFrom(loadedRepo3);
            return true;
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error loading file: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Saves simulation data to the default simulation file.
     *
     * @return true if the data was successfully saved; false if an error occurred during saving
     */
    public boolean save() { return save(FILE_NAME); }

    public boolean save(String fileName) { return save(new File(fileName)); }

    public boolean save(File file) {
        SimulationRepository simulationRepo = Repositories.getInstance().getSimulationRepository();
        StationRepository stationRepo = Repositories.getInstance().getStationRepository();
        BuildingRepository buildingRepo = Repositories.getInstance().getBuildingRepository();
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

            try {
                out.writeObject(simulationRepo);
                out.writeObject(stationRepo);
                out.writeObject(buildingRepo);
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;
        }
    }
}
