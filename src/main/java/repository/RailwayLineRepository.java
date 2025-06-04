package repository;

import domain.Map;
import domain.RailwayLine;
import domain.Scenario;
import domain.Station;
import java.util.ArrayList;
import java.util.List;

public class RailwayLineRepository {
    private List<RailwayLine> railwayLines = new ArrayList<>();

    public void save(RailwayLine railwayLine) {
        railwayLines.add(railwayLine);
    }

    public List<RailwayLine> getRailwayLines() {
        return new ArrayList<>(railwayLines);
    }

    public boolean connectionExists(Station from, Station to) {
        return railwayLines.stream().anyMatch(rl ->
                (rl.getFromStation().equals(from) && rl.getToStation().equals(to)) ||
                        (rl.getFromStation().equals(to) && rl.getToStation().equals(from))
        );
    }

    public long countByScenarioMapAndOwner(Scenario scenario, Map map, String ownerMail) {
        return railwayLines.stream().filter(r -> r.getScenario().equals(scenario) && r.getMap().equals(map) && r.getOwnerMail().equals(ownerMail)).count();
    }

}