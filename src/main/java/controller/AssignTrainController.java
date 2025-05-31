package controller;

import domain.*;
import repository.Repositories;
import repository.TrainRepository;
import repository.CarriageRepository;
import repository.RailwayLineRepository;
import repository.RouteRepository;

import java.util.ArrayList;
import java.util.List;

public class AssignTrainController {

    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;
    private static final double PEOPLE_RATIO = 0.7;

    private final RailwayLineRepository railwayLineRepo = Repositories.getInstance().getRailwayLineRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();

    public int createTrainAndAssignToRoute(Locomotive locomotive, List<PointOfRoute> route,
                                           int peopleCarriageCount, int mailCarriageCount,
                                           Scenario scenario, Budget budget, String playerEmail) {

        List<Carriage> carriages = getCarriages(peopleCarriageCount, mailCarriageCount);

        Train train = new Train(locomotive, carriages, playerEmail);
        TrainRepository trainRepo = Repositories.getInstance().getTrainRepository();
        trainRepo.save(train);


        return assignTrainToRoute(train, route, scenario, budget, playerEmail);
    }

    private static List<Carriage> getCarriages(int peopleCarriageCount, int mailCarriageCount) {
        List<Carriage> carriages = new ArrayList<>();
        CarriageRepository carriageRepo = Repositories.getInstance().getCarriageRepository();

        for (int i = 0; i < peopleCarriageCount; i++) {
            Carriage c = new Carriage(Carriage.Type.PEOPLE);
            carriageRepo.save(c);
            carriages.add(c);
        }

        for (int i = 0; i < mailCarriageCount; i++) {
            Carriage c = new Carriage(Carriage.Type.MAIL);
            carriageRepo.save(c);
            carriages.add(c);
        }
        return carriages;
    }

    public int assignTrainToRoute(Train train, List<PointOfRoute> route, Scenario scenario, Budget budget, String playerEmail) {
        int totalPeopleCollected = 0, totalMailCollected = 0;
        List<Carriage> carriages = train.getCarriages();

        carriages.forEach(c -> c.setCapacityOwned(0));

        createRailwayLines(route);
        Route routeEntity = new Route(route, playerEmail,
                route.get(0).getStation().getMapName(),
                route.get(0).getStation().getScenarioName());
        routeRepo.save(routeEntity);

        for (PointOfRoute pointOfRoute : route) {
            if (isTrainFull(carriages)) break;

            Station station = pointOfRoute.getStation();
            List<Carriage.Type> cargoTypes = pointOfRoute.getCargoTypes();

            int stationCapacity = station.getCapacityOwned();
            if (stationCapacity <= 0) continue;

            int availablePeople = (int) Math.round(stationCapacity * PEOPLE_RATIO);
            int availableMail = stationCapacity - availablePeople;

            if (cargoTypes.contains(Carriage.Type.PEOPLE)) {
                int collected = collectCargo(carriages, Carriage.Type.PEOPLE, availablePeople);
                totalPeopleCollected += collected;
                availablePeople -= collected;
            }

            if (cargoTypes.contains(Carriage.Type.MAIL)) {
                int collected = collectCargo(carriages, Carriage.Type.MAIL, availableMail);
                totalMailCollected += collected;
                availableMail -= collected;
            }

            station.setCapacityOwned(availablePeople + availableMail);
        }

        boolean isVaccinationCampaign = scenario.getHistoricalRestrictions()
                .stream().anyMatch(r -> r.equalsIgnoreCase("vaccination campaign"));

        int moneyWon = isVaccinationCampaign ?
                ((totalPeopleCollected * PEOPLE_VALUE) * 2) + (totalMailCollected * MAIL_VALUE) :
                (totalPeopleCollected * PEOPLE_VALUE) + (totalMailCollected * MAIL_VALUE);

        System.out.println("\n=== Train Route Summary ===");
        System.out.println("Total cargo collected: " + (totalPeopleCollected + totalMailCollected));
        System.out.println("People collected: " + totalPeopleCollected);
        System.out.println("Mail collected: " + totalMailCollected);
        System.out.println("Budget earned: " + moneyWon);
        System.out.println("=========================\n");

        budget.addBudget(moneyWon);
        return moneyWon;
    }

    private void createRailwayLines(List<PointOfRoute> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            Station from = route.get(i).getStation();
            Station to = route.get(i + 1).getStation();

            if (!railwayLineRepo.connectionExists(from, to)) {
                railwayLineRepo.save(new RailwayLine(from, to));
            }
        }
    }

    private boolean isTrainFull(List<Carriage> carriages) {
        return carriages.stream().allMatch(c -> c.getCapacityOwned() >= c.getCapacity());
    }

    private int collectCargo(List<Carriage> carriages, Carriage.Type cargoType, int availableCargo) {
        int totalCollected = 0;
        int remainingCargo = availableCargo;

        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType && remainingCargo > 0) {
                int spaceLeft = carriage.getCapacity() - carriage.getCapacityOwned();
                if (spaceLeft > 0) {
                    int toCollect = Math.min(remainingCargo, spaceLeft);
                    carriage.setCapacityOwned(carriage.getCapacityOwned() + toCollect);
                    totalCollected += toCollect;
                    remainingCargo -= toCollect;
                }
            }
        }

        return totalCollected;
    }
}