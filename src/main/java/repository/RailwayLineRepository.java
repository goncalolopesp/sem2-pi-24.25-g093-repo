package repository;

import domain.Map;
import domain.RailwayLine;
import domain.Scenario;
import domain.Station;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing and storing instances of RailwayLine.
 * Provides functionality to save new RailwayLine instances, retrieve all stored railway lines,
 * check for connections between stations, and count railway lines based on specific criteria.
 */
public class RailwayLineRepository {
    private List<RailwayLine> railwayLines = new ArrayList<>();

    /**
     * Saves a given RailwayLine instance to the repository.
     *
     * @param railwayLine the RailwayLine instance to be added to the repository
     */
    public void save(RailwayLine railwayLine) {
        railwayLines.add(railwayLine);
    }

    /**
     * Retrieves all railway lines stored in the repository.
     *
     * @return a list containing all RailwayLine instances managed by the repository
     */
    public List<RailwayLine> getRailwayLines() {
        return new ArrayList<>(railwayLines);
    }

    /**
     * Checks if a direct connection*/
    public boolean connectionExists(Station from, Station to) {
        return railwayLines.stream().anyMatch(rl ->
                (rl.getFromStation().equals(from) && rl.getToStation().equals(to)) ||
                        (rl.getFromStation().equals(to) && rl.getToStation().equals(from))
        );
    }

    /**
     * Counts the number of railway lines that match the specified scenario, map, and owner email address.
     *
     * @param scenario the Scenario to filter railway lines by
     * @param map the Map to filter railway lines by
     * @param ownerMail the email address of the owner to filter railway lines by
     * @return the total count of railway lines that match the provided scenario, map, and owner email address
     */
    public long countByScenarioMapAndOwner(Scenario scenario, Map map, String ownerMail) {
        return railwayLines.stream().filter(r -> r.getScenario().equals(scenario) && r.getMap().equals(map) && r.getOwnerMail().equals(ownerMail)).count();
    }

}