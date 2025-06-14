package controller;

import domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import repository.*;
import ui.console.menu.MenuItem;

import domain.User;
import domain.City;
import domain.CurrentUser;
import domain.Map;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimulationController {

    private static final int PEOPLE_VALUE = 5;
    private static final int MAIL_VALUE = 3;
    private static final int COAL_VALUE = 4;
    private static final int IRON_VALUE = 6;
    private static final int STEEL_VALUE = 10;
    private static final int KMS_FUEL = 100;
    private static final int AMOUNT_PER_LINE = 5;
    private static final int PORT_BEHAVIOUR = 1;

    private static int yearlyEarnings = 0;
    private static int yearlyMaintenanceCost = 0;
    private static int yearlyFuelCost = 0;
    private static int yearlyTrackMaintenanceCost = 0;
    private static int yearlyTotalCost = 0;

    private final java.util.Map<String, TrainJourney> activeJourneys = new HashMap<>();
    private java.util.Map<String, Integer> waitingTrainsAtStation = new ConcurrentHashMap<>();
    private java.util.Map<String, Boolean> trainsPreservingCargo = new ConcurrentHashMap<>();


    private final User currentUser = CurrentUser.getInstance().getUser();
    private final SimulationRepository simulationRepo = Repositories.getInstance().getSimulationRepository();
    private final MapRepository mapRepo = Repositories.getInstance().getMapRepository();
    private final IndustryRepository industryRepo = Repositories.getInstance().getIndustryRepository();
    private final CityRepository cityRepo = Repositories.getInstance().getCityRepository();
    private final ScenarioRepository scenarioRepo = Repositories.getInstance().getScenarioRepository();
    private final StationRepository stationrepo = Repositories.getInstance().getStationRepository();
    private final BuildingRepository buildingRepo = Repositories.getInstance().getBuildingRepository();
    private final BudgetRepository budgetRepo = Repositories.getInstance().getBudgetRepository();
    private final RouteRepository routeRepo = Repositories.getInstance().getRouteRepository();
    private final RailwayLineRepository railwayRepo = Repositories.getInstance().getRailwayLineRepository();

    private static class TrainJourney {
        private final Route route;
        private final int departureYear;
        private final int estimatedArrivalYear;
        private boolean completed;

        public TrainJourney(Route route, int currentSimulationYear) {
            this.route = route;
            this.departureYear = currentSimulationYear;
            this.completed = false;
            this.estimatedArrivalYear = calculateEstimatedArrivalYear(currentSimulationYear) + 1;
        }

        private int calculateEstimatedArrivalYear(int currentYear) {
            if (route.getPoints().size() < 2 || route.getTrain() == null || route.getTrain().getLocomotive() == null) {
                return currentYear;
            }

            double totalDistance = 0.0;
            Locomotive locomotive = route.getTrain().getLocomotive();

            for (int i = 0; i < route.getPoints().size() - 1; i++) {
                Station currentStation = route.getPoints().get(i).getStation();
                Station nextStation = route.getPoints().get(i + 1).getStation();

                RailwayLine railwayLine = findRailwayLineBetweenStations(currentStation, nextStation, currentStation.getOwnerPlayer());
                if (railwayLine != null) {
                    totalDistance += railwayLine.getDistance();
                }
            }

            double baseSpeed = locomotive.getTopSpeed();
            double speedDegradation = calculateSpeedDegradation();
            double effectiveSpeed = baseSpeed * (1 - speedDegradation);

            double travelTimeYears = totalDistance / effectiveSpeed;

            int yearsToTravel = Math.max(1, (int) Math.ceil(travelTimeYears));

            return currentYear + yearsToTravel;
        }

        private double calculateSpeedDegradation() {
            if (route.getTrain() == null || route.getTrain().getCarriages() == null) {
                return 0.0;
            }

            List<Carriage> carriages = route.getTrain().getCarriages();
            if (carriages.isEmpty()) {
                return 0.0;
            }

            double totalDegradation = carriages.size() * 0.05;
            return Math.min(totalDegradation, 0.30);
        }

        private RailwayLine findRailwayLineBetweenStations(Station station1, Station station2, String owner) {
            return Repositories.getInstance().getRailwayLineRepository()
                    .getRailwayLines()
                    .stream()
                    .filter(line -> line.getOwnerMail().equals(owner))
                    .filter(line ->
                            (line.getFromStation().equals(station1) && line.getToStation().equals(station2)) ||
                                    (line.getFromStation().equals(station2) && line.getToStation().equals(station1))
                    )
                    .findFirst()
                    .orElse(null);
        }
        public Route getRoute() { return route; }
        public int getDepartureYear() { return departureYear; }
        public int getEstimatedArrivalYear() { return estimatedArrivalYear; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    public class DemandManager {
        private static final int MIN_DEMAND = 1;
        private static final int MAX_DEMAND = 9;
        private static final int DEFAULT_DEMAND = 4;

        private static final int LOW_DELIVERY_THRESHOLD = 10;  // If less than this delivered, demand increases
        private static final int HIGH_DELIVERY_THRESHOLD = 50; // If more than this delivered, demand decreases

        private int peopleDemand = DEFAULT_DEMAND;
        private int mailDemand = DEFAULT_DEMAND;
        private int coalDemand = DEFAULT_DEMAND;
        private int ironDemand = DEFAULT_DEMAND;
        private int steelDemand = DEFAULT_DEMAND;

        private int yearlyPeopleDelivered = 0;
        private int yearlyMailDelivered = 0;
        private int yearlyCoalDelivered = 0;
        private int yearlyIronDelivered = 0;
        private int yearlySteelDelivered = 0;

        public int getPeopleDemand() { return peopleDemand; }
        public int getMailDemand() { return mailDemand; }
        public int getCoalDemand() { return coalDemand; }
        public int getIronDemand() { return ironDemand; }
        public int getSteelDemand() { return steelDemand; }

        public void recordDelivery(Carriage.Type cargoType, int amount) {
            switch (cargoType) {
                case PEOPLE:
                    yearlyPeopleDelivered += amount;
                    break;
                case MAIL:
                    yearlyMailDelivered += amount;
                    break;
                case COAL:
                    yearlyCoalDelivered += amount;
                    break;
                case IRON:
                    yearlyIronDelivered += amount;
                    break;
                case STEEL:
                    yearlySteelDelivered += amount;
                    break;
            }
        }

        public void updateYearlyDemand(Scenario scenario) {
            peopleDemand = calculateNewDemand(peopleDemand, yearlyPeopleDelivered);
            mailDemand = calculateNewDemand(mailDemand, yearlyMailDelivered);
            coalDemand = calculateNewDemand(coalDemand, yearlyCoalDelivered);
            ironDemand = calculateNewDemand(ironDemand, yearlyIronDelivered);
            steelDemand = calculateNewDemand(steelDemand, yearlySteelDelivered);

            applyScenarioModifications(scenario);

            resetYearlyDeliveries();

            displayDemandUpdate();
        }

        private int calculateNewDemand(int currentDemand, int yearlyDelivered) {
            int newDemand = currentDemand;

            if (yearlyDelivered < LOW_DELIVERY_THRESHOLD) {
                newDemand += 1;
            } else if (yearlyDelivered > HIGH_DELIVERY_THRESHOLD) {
                newDemand -= 1;
            }

            // Apply some randomness (±1)
            Random random = new Random();
            newDemand += random.nextInt(3) - 1; // -1, 0, or +1

            return Math.max(MIN_DEMAND, Math.min(MAX_DEMAND, newDemand));
        }

        private void applyScenarioModifications(Scenario scenario) {
            if (hasHistoricalRestriction(scenario, "industrial revolution")) {
                coalDemand = Math.min(MAX_DEMAND, coalDemand + 2);
                ironDemand = Math.min(MAX_DEMAND, ironDemand + 2);
                steelDemand = Math.min(MAX_DEMAND, steelDemand + 3);
            }

            if (hasHistoricalRestriction(scenario, "vaccination campaign")) {
                peopleDemand = Math.min(MAX_DEMAND, peopleDemand + 1);
            }

            if (hasHistoricalRestriction(scenario, "economic depression")) {
                peopleDemand = Math.max(MIN_DEMAND, peopleDemand - 1);
                mailDemand = Math.max(MIN_DEMAND, mailDemand - 1);
            }

            if (hasHistoricalRestriction(scenario, "war time")) {
                steelDemand = Math.min(MAX_DEMAND, steelDemand + 2);
                ironDemand = Math.min(MAX_DEMAND, ironDemand + 1);
                peopleDemand = Math.max(MIN_DEMAND, peopleDemand - 1);
            }
        }

        private boolean hasHistoricalRestriction(Scenario scenario, String restriction) {
            return scenario.getHistoricalRestrictions().stream()
                    .anyMatch(r -> r.equalsIgnoreCase(restriction));
        }

        private void resetYearlyDeliveries() {
            yearlyPeopleDelivered = 0;
            yearlyMailDelivered = 0;
            yearlyCoalDelivered = 0;
            yearlyIronDelivered = 0;
            yearlySteelDelivered = 0;
        }

        private void displayDemandUpdate() {
            System.out.println("\n[Yearly Demand Update] Demand levels updated for the new year.");
            System.out.printf("People: %d, Mail: %d, Coal: %d, Iron: %d, Steel: %d%n",
                    peopleDemand, mailDemand, coalDemand, ironDemand, steelDemand);
        }
    }

    private final DemandManager demandManager = new DemandManager();

    public Runnable executeRoutes(Map selectedMap, Scenario selectedScenario, Budget budget,
                                  AtomicBoolean running, int currentSimulationYear) {
        return () -> {
            if (!running.get() || currentUser == null) {
                return;
            }

            List<Route> userRoutes = getUserRoutes(selectedMap, selectedScenario);

            if (userRoutes.isEmpty()) {
                return;
            }

            for (Route route : userRoutes) {

                if (!isRouteValid(route)) {
                    continue;
                }

                String routeId = generateRouteId(route);
                if (!activeJourneys.containsKey(routeId)) {
                    startTrainJourney(route, currentSimulationYear);
                }
            }

            checkArrivingTrains(selectedScenario, budget, selectedMap, currentSimulationYear);
        };
    }

    private boolean isRouteValid(Route route) {
        if (route == null) {
            System.out.println("Route is null");
            return false;
        }

        if (route.getTrain() == null) {
            System.out.println("Route has no train assigned");
            return false;
        }

        if (route.getPoints() == null || route.getPoints().size() < 2) {
            System.out.println("Route must have at least 2 points");
            return false;
        }

        for (int i = 0; i < route.getPoints().size() - 1; i++) {
            Station current = route.getPoints().get(i).getStation();
            Station next = route.getPoints().get(i + 1).getStation();

            RailwayLine connection = findRailwayLineBetweenStations(current, next, currentUser.getEmail());
            if (connection == null) {
                System.out.printf("No railway connection between %s and %s%n",
                        current.getName(), next.getName());
                return false;
            }
        }

        return true;
    }

    private void startTrainJourney(Route route, int currentSimulationYear) {
        TrainJourney journey = new TrainJourney(route, currentSimulationYear);
        String routeId = generateRouteId(route);

        activeJourneys.put(routeId, journey);

        System.out.printf("\n[Train Departure] Train departed from %s in year %d. Estimated arrival: year %d. Type your option: ",
                route.getPoints().get(0).getStation().getName(),
                journey.getDepartureYear(),
                journey.getEstimatedArrivalYear());
    }

    private void checkArrivingTrains(Scenario scenario, Budget budget, Map selectedMap, int currentSimulationYear) {
        List<String> completedJourneys = new ArrayList<>();

        for (java.util.Map.Entry<String, TrainJourney> entry : activeJourneys.entrySet()) {
            TrainJourney journey = entry.getValue();
            String routeId = entry.getKey();

            if (waitingTrainsAtStation.containsKey(routeId)) {
                executeArrivingRoute(journey.getRoute(), scenario, budget, selectedMap, journey, currentSimulationYear);
                if (!waitingTrainsAtStation.containsKey(routeId)) {
                    journey.setCompleted(true);
                    completedJourneys.add(routeId);
                }
                continue;
            }

            if (!journey.isCompleted() && currentSimulationYear >= journey.getEstimatedArrivalYear()) {
                executeArrivingRoute(journey.getRoute(), scenario, budget, selectedMap, journey, currentSimulationYear);
                if (!waitingTrainsAtStation.containsKey(routeId)) {
                    journey.setCompleted(true);
                    completedJourneys.add(routeId);
                }
            }
        }

        for (String completedId : completedJourneys) {
            activeJourneys.remove(completedId);
            waitingTrainsAtStation.remove(completedId);
            trainsPreservingCargo.remove(completedId);
        }
    }

    private void displayRouteCompleteWithYear(CargoCollectionResult result, int totalEarnings, TrainJourney journey, int currentYear) {
        System.out.printf("\n[Train Arrival] Train arrived in year %d (departed year %d). Total Cargo: %d (People: %d, Mail: %d, Iron: %d, Coal: %d, Steel: %d) - Earned: %d. Type your option: ",
                currentYear, journey.getDepartureYear(), result.getTotalCargo(), result.people, result.mail, result.iron, result.coal, result.steel, totalEarnings);
    }

    private int calculateArrivalEarnings(CargoCollectionResult result, Scenario scenario, Route route, Map selectedMap, AtomicReference<Double> peopleEarningMultiplier) {
        boolean isVaccinationCampaign = hasHistoricalRestriction(scenario, "vaccination campaign");
        final float DEFAULT_DEMAND = 4f;

        double routeDistance = calculateRouteDistance(route, selectedMap);
        double distanceMultiplier = Math.max(0.5, Math.min(2.0, routeDistance / 100.0));

        int peopleEarnings = (int)(
                result.people
                        * PEOPLE_VALUE
                        * peopleEarningMultiplier.get()
                        * (isVaccinationCampaign ? 2 : 1)
                        * demandManager.getPeopleDemand() / DEFAULT_DEMAND
                        * distanceMultiplier
        );

        int mailEarnings  = (int)(result.mail  * MAIL_VALUE  * (demandManager.getMailDemand() / DEFAULT_DEMAND) * distanceMultiplier);
        int coalEarnings  = (int)(result.coal  * COAL_VALUE  * (demandManager.getCoalDemand() / DEFAULT_DEMAND) * distanceMultiplier);
        int ironEarnings  = (int)(result.iron  * IRON_VALUE  * (demandManager.getIronDemand() / DEFAULT_DEMAND) * distanceMultiplier);
        int steelEarnings = (int)(result.steel * STEEL_VALUE * (demandManager.getSteelDemand() / DEFAULT_DEMAND) * distanceMultiplier);

        return peopleEarnings + mailEarnings + coalEarnings + ironEarnings + steelEarnings;
    }

    private double calculateRouteDistance(Route route, Map selectedMap) {
        if (route.getPoints().size() < 2) return 0.0;

        double totalDistance = 0.0;
        for (int i = 0; i < route.getPoints().size() - 1; i++) {
            Station currentStation = route.getPoints().get(i).getStation();
            Station nextStation = route.getPoints().get(i + 1).getStation();

            RailwayLine railwayLine = findRailwayLineBetweenStations(currentStation, nextStation, currentUser.getEmail());
            if (railwayLine != null) {
                totalDistance += railwayLine.getDistance();
            }
        }
        return totalDistance * selectedMap.getScale();
    }

    private String generateRouteId(Route route) {
        return route.getMapName() + "_" + route.getScenarioName() + "_" + route.getPlayerEmail() + "_" +
                route.getPoints().hashCode() + "_" + (route.getTrain() != null ? route.getTrain().hashCode() : 0);
    }

    private List<Route> getUserRoutes(Map map, Scenario scenario) {
        return routeRepo.getRoutes().stream()
                .filter(r -> r.getPlayerEmail().equals(currentUser.getEmail())
                        && r.getMapName().equals(map.getMapName())
                        && r.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    private int calculateRouteFuelCost(Route route, Map selectedMap) {
        if (route.getPoints().size() < 2) return 0;

        Train train = route.getTrain();
        if (train == null || train.getLocomotive() == null) return 0;

        Locomotive locomotive = train.getLocomotive();
        double totalDistance = 0.0;

        for (int i = 0; i < route.getPoints().size() - 1; i++) {
            Station currentStation = route.getPoints().get(i).getStation();
            Station nextStation = route.getPoints().get(i + 1).getStation();

            RailwayLine railwayLine = findRailwayLineBetweenStations(currentStation, nextStation, currentUser.getEmail());
            if (railwayLine != null) {
                totalDistance += railwayLine.getDistance();
            }
        }

        double mapScale = selectedMap.getScale();
        int fuelCost = (int) (locomotive.getFuelCost() * totalDistance * mapScale)/ KMS_FUEL;

        return fuelCost;
    }

    private RailwayLine findRailwayLineBetweenStations(Station station1, Station station2, String owner) {
        return Repositories.getInstance().getRailwayLineRepository()
                .getRailwayLines()
                .stream()
                .filter(line -> line.getOwnerMail().equals(owner))
                .filter(line ->
                        (line.getFromStation().equals(station1) && line.getToStation().equals(station2))
                )
                .findFirst()
                .orElse(null);
    }

    private void resetCarriageCapacity(List<Carriage> carriages) {
        carriages.forEach(c -> c.setCapacityOwned(0));
    }

    private void deliverToSteelMills(Route route, List<Carriage> carriages, CargoCollectionResult result) {
        Map selectedMap = mapRepo.getMaps().stream()
                .filter(map -> map.getMapName().equals(route.getMapName()))
                .findFirst()
                .orElse(null);

        if (selectedMap == null) {
            return;
        }

        List<Industry> steelMillIndustries = industryRepo.getIndustries().stream()
                .filter(industry -> industry.getType().equals(IndustryType.STEEL_MILL))
                .filter(industry -> industry.getMapName().equals(selectedMap.getMapName()))
                .collect(Collectors.toList());

        for (Industry steelMill : steelMillIndustries) {
            if (!result.hasIronAndCoal()) {
                break;
            }

            deliverCargoToSteelMill(carriages, result, steelMill);
        }
    }

    private void deliverCargoToSteelMill(List<Carriage> carriages, CargoCollectionResult result, Industry steelMill) {

        int ironDelivered = removeCargoFromTrain(carriages, Carriage.Type.IRON);
        int coalDelivered = removeCargoFromTrain(carriages, Carriage.Type.COAL);

        result.iron -= ironDelivered;
        result.coal -= coalDelivered;

        steelMill.setStoredIron(steelMill.getStoredIron() + ironDelivered);
        steelMill.setStoredCoal(steelMill.getStoredCoal() + coalDelivered);

        if (ironDelivered > 0 || coalDelivered > 0) {
            System.out.printf("\nDelivered %d iron and %d coal to steel mill at location (%d, %d). Type your option: ",
                    ironDelivered, coalDelivered, steelMill.getLocation().getX(), steelMill.getLocation().getY());
        }
    }

    private int removeCargoFromTrain(List<Carriage> carriages, Carriage.Type cargoType) {
        int totalRemoved = 0;
        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType) {
                totalRemoved += carriage.getCapacityOwned();
                carriage.setCapacityOwned(0);
            }
        }
        return totalRemoved;
    }

    private void collectCargoFromStation(Route route, Station station, List<Carriage.Type> requestedTypes,
                                         CargoCollectionResult result) {

        for (Carriage.Type cargoType : requestedTypes) {
            int availableCargo = getAvailableCargoAtStation(station, cargoType);

            if (availableCargo > 0) {
                int collected = collectCargoFromSingleStation(route, station, availableCargo, cargoType);
                System.out.println("\n[Cargo Collection] " + collected + " " + cargoType + " cargo collected from station " + station.getName());

                if (collected > 0) {
                    updateStationCargo(station, cargoType, collected);
                    updateCollectionResult(result, cargoType, collected);
                }
            }
        }

        updateStationCapacity(station);
    }

    private int processCargoAtSingleStation(Route route, PointOfRoute stationPoint,
                                            int maxCargoTypeCapacity, int availableCargo, Carriage.Type cargoType) {

        CargoMode cargoMode = stationPoint.getCargoMode();
        Train train = route.getTrain();
        int currentCargoInTrain = getCurrentCargoInTrain(train, cargoType);

        int requiredCargo = calculateRequiredCargoForMode(cargoMode, maxCargoTypeCapacity, availableCargo);

        int targetAmount;
        switch (cargoMode) {
            case FULL:
                targetAmount = maxCargoTypeCapacity - currentCargoInTrain;
                break;
            case HALF:
                int halfCapacity = (int) Math.ceil(maxCargoTypeCapacity * 0.5);
                targetAmount = halfCapacity - currentCargoInTrain;
                break;
            case AVAILABLE:
                targetAmount = availableCargo;
                break;
            default:
                targetAmount = requiredCargo;
                break;
        }

        targetAmount = Math.max(0, targetAmount);

        if (availableCargo < targetAmount) {
            return 0;
        }

        return Math.min(targetAmount, availableCargo);
    }

    private int calculateRequiredCargoForMode(CargoMode cargoMode, int maxCargoTypeCapacity, int availableCargo) {
        switch (cargoMode) {
            case FULL:
                return maxCargoTypeCapacity;

            case HALF:
                return (int) Math.ceil(maxCargoTypeCapacity * 0.5);

            case AVAILABLE:
                return availableCargo;

            default:
                throw new IllegalArgumentException("Unknown cargo mode: " + cargoMode);
        }
    }

    private boolean canTrainDepartFromStation(Route route, int stationIndex) {
        PointOfRoute stationPoint = route.getPoints().get(stationIndex);
        Station station = stationPoint.getStation();
        CargoMode cargoMode = stationPoint.getCargoMode();
        List<Carriage.Type> requestedTypes = stationPoint.getCargoTypes();
        Train train = route.getTrain();

        switch (cargoMode) {
            case AVAILABLE:
                collectAvailableCargoForWaiting(route, station, requestedTypes);
                return true;

            case HALF:
                collectAvailableCargoForWaiting(route, station, requestedTypes);
                return requestedTypes.stream()
                        .allMatch(type -> {
                            int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                            int halfCapacity = (int) Math.ceil(maxCapacityForType * 0.5);
                            int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                            return currentCargoInTrain >= halfCapacity;
                        });

            case FULL:
                collectAvailableCargoForWaiting(route, station, requestedTypes);
                return requestedTypes.stream()
                        .allMatch(type -> {
                            int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                            int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                            return currentCargoInTrain >= maxCapacityForType;
                        });

            default:
                return false;
        }
    }


    private void collectAvailableCargoForWaiting(Route route, Station station, List<Carriage.Type> requestedTypes) {
        Train train = route.getTrain();

        PointOfRoute currentStationPoint = route.getPoints().stream()
                .filter(point -> point.getStation().equals(station))
                .findFirst()
                .orElse(null);

        if (currentStationPoint == null) return;

        CargoMode cargoMode = currentStationPoint.getCargoMode();

        for (Carriage.Type cargoType : requestedTypes) {
            int availableCargo = getAvailableCargoAtStation(station, cargoType);
            int maxCargoTypeCapacity = calculateCargoTypeCapacity(train, cargoType);
            int currentCargoInTrain = getCurrentCargoInTrain(train, cargoType);

            int targetCapacity;
            switch (cargoMode) {
                case HALF:
                    targetCapacity = (int) Math.ceil(maxCargoTypeCapacity * 0.5);
                    break;
                case AVAILABLE:
                    targetCapacity = currentCargoInTrain + Math.min(availableCargo, maxCargoTypeCapacity - currentCargoInTrain);
                    break;
                default:
                    targetCapacity = maxCargoTypeCapacity;
                    break;
            }

            int remainingCapacity = targetCapacity - currentCargoInTrain;

            if (availableCargo > 0 && remainingCapacity > 0) {
                int toCollect = Math.min(availableCargo, remainingCapacity);

                loadCargoIntoCarriages(train.getCarriages(), cargoType, toCollect);

                updateStationCargo(station, cargoType, toCollect);

                System.out.println("\n[Partial Cargo Collection] " + toCollect + " " + cargoType + " cargo collected while waiting at " + station.getName() +
                        " (Mode: " + cargoMode + ", Target: " + targetCapacity + ", Current: " + (currentCargoInTrain + toCollect) + ")");
            }
        }

        updateStationCapacity(station);
    }

    // Helper method to get current cargo in train for a specific type
    private int getCurrentCargoInTrain(Train train, Carriage.Type cargoType) {
        return train.getCarriages().stream()
                .filter(carriage -> carriage.getType() == cargoType)
                .mapToInt(Carriage::getCapacityOwned)
                .sum();
    }

    private void loadCargoIntoCarriages(List<Carriage> carriages, Carriage.Type cargoType, int amountToLoad) {
        int remaining = amountToLoad;

        for (Carriage carriage : carriages) {
            if (carriage.getType() == cargoType && remaining > 0) {
                int availableSpace = carriage.getCapacity() - carriage.getCapacityOwned();
                int toLoad = Math.min(remaining, availableSpace);

                if (toLoad > 0) {
                    carriage.setCapacityOwned(carriage.getCapacityOwned() + toLoad);
                    remaining -= toLoad;
                    System.out.println("\nLoaded " + toLoad + " " + cargoType + " into carriage. Carriage now has: " + carriage.getCapacityOwned() + "/" + carriage.getCapacity());
                }
            }
        }

        if (remaining > 0) {
            System.out.println("\nWarning: Could not load " + remaining + " " + cargoType + " - insufficient carriage capacity");
        }
    }

    private void executeArrivingRoute(Route route, Scenario scenario, Budget budget, Map selectedMap, TrainJourney journey, int currentYear) {
        Train train = route.getTrain();
        if (train == null) return;

        List<Carriage> carriages = train.getCarriages();
        String routeId = generateRouteId(route);

        if (!trainsPreservingCargo.getOrDefault(routeId, false)) {
            resetCarriageCapacity(carriages);
        }

        trainsPreservingCargo.remove(routeId);

        CargoCollectionResult result = new CargoCollectionResult();

        int routeFuelCost = calculateRouteFuelCost(route, selectedMap);
        yearlyFuelCost += routeFuelCost;

        AtomicReference<Double> peopleEarningMultiplier = new AtomicReference<>(1.0);

        int startingStation = waitingTrainsAtStation.getOrDefault(routeId, 0);

        for (int i = startingStation; i < route.getPoints().size(); i++) {
            PointOfRoute point = route.getPoints().get(i);
            Station station = point.getStation();

            if (station.getCapacityOwned() <= 0) continue;

            if (!canTrainDepartFromStation(route, i)) {
                waitingTrainsAtStation.put(routeId, i);
                trainsPreservingCargo.put(routeId, true);

                System.out.println("\n\nPeople: " + station.getPeopleStored() + " Mail: " + station.getMailStored());
                System.out.printf("[Waiting] Train waiting at %s for sufficient cargo (%s mode). Type your option: ",
                        station.getName(), point.getCargoMode());
                return;
            } else {
                waitingTrainsAtStation.remove(routeId);
            }

            if (isTrainFull(carriages)) break;

            // Collect cargo and update result
            if (station.getPeopleStored() > 0) {
                buildingRepo.getBuildings().stream()
                        .filter(building -> building.getStation().equals(station))
                        .forEach(building -> {
                            if (building.getBuildingType().contains("cafe")) {
                                peopleEarningMultiplier.set(peopleEarningMultiplier.get() + 0.2);
                            }
                            if (building.getBuildingType().contains("hotel")) {
                                peopleEarningMultiplier.set(peopleEarningMultiplier.get() + 0.3);
                            }
                        });
            }

            collectCargoFromStation(route, station, point.getCargoTypes(), result);
        }

        addTrainCargoToResult(train, result);

        if (result.people > 0) demandManager.recordDelivery(Carriage.Type.PEOPLE, result.people);
        if (result.mail > 0) demandManager.recordDelivery(Carriage.Type.MAIL, result.mail);
        if (result.coal > 0) demandManager.recordDelivery(Carriage.Type.COAL, result.coal);
        if (result.iron > 0) demandManager.recordDelivery(Carriage.Type.IRON, result.iron);
        if (result.steel > 0) demandManager.recordDelivery(Carriage.Type.STEEL, result.steel);

        int totalEarnings = (int) ((calculateArrivalEarnings(result, scenario, route, selectedMap, peopleEarningMultiplier)) * scenario.getProfitMultiplier());

        if (result.getTotalCargo() > 0) {
            displayRouteCompleteWithYear(result, totalEarnings, journey, currentYear);
            budget.addBudget(totalEarnings - (routeFuelCost / KMS_FUEL));
            yearlyEarnings += totalEarnings;
        }

        if (result.hasIronAndCoal()) {
            deliverToSteelMills(route, carriages, result);
        }

        waitingTrainsAtStation.remove(routeId);
        trainsPreservingCargo.remove(routeId);
    }

    private void addTrainCargoToResult(Train train, CargoCollectionResult result) {
        for (Carriage carriage : train.getCarriages()) {
            switch (carriage.getType()) {
                case PEOPLE:
                    result.people += carriage.getCapacityOwned();
                    break;
                case MAIL:
                    result.mail += carriage.getCapacityOwned();
                    break;
                case COAL:
                    result.coal += carriage.getCapacityOwned();
                    break;
                case IRON:
                    result.iron += carriage.getCapacityOwned();
                    break;
                case STEEL:
                    result.steel += carriage.getCapacityOwned();
                    break;
            }
        }
    }

    private int collectCargoFromSingleStation(Route route, Station station, int availableCargo, Carriage.Type cargoType) {
        if (route == null) {
            return 0;
        }

        Train train = route.getTrain();
        int maxCargoTypeCapacity = calculateCargoTypeCapacity(train, cargoType);

        if (maxCargoTypeCapacity == 0) {
            return 0;
        }

        PointOfRoute stationPoint = findStationInRoute(route, station);
        if (stationPoint == null) {
            return 0;
        }

        return processCargoAtSingleStation(route, stationPoint, maxCargoTypeCapacity, availableCargo, cargoType);
    }

    private PointOfRoute findStationInRoute(Route route, Station targetStation) {
        return route.getPoints().stream()
                .filter(point -> point.getStation().equals(targetStation))
                .findFirst()
                .orElse(null);
    }

    private int calculateCargoTypeCapacity(Train train, Carriage.Type cargoType) {
        return train.getCarriages().stream()
                .filter(carriage -> carriage.getType() == cargoType)
                .mapToInt(Carriage::getCapacity)
                .sum();
    }

    private int getAvailableCargoAtStation(Station station, Carriage.Type cargoType) {
        switch (cargoType) {
            case PEOPLE: return station.getPeopleStored();
            case MAIL: return station.getMailStored();
            case COAL: return station.getCoalStored();
            case IRON: return station.getIronStored();
            case STEEL: return station.getSteelStored();
            default: return 0;
        }
    }

    private void updateStationCargo(Station station, Carriage.Type cargoType, int collectedAmount) {
        switch (cargoType) {
            case PEOPLE:
                station.setPeopleStored(Math.max(0, station.getPeopleStored() - collectedAmount));
                break;
            case MAIL:
                station.setMailStored(Math.max(0, station.getMailStored() - collectedAmount));
                break;
            case COAL:
                station.setCoalStored(Math.max(0, station.getCoalStored() - collectedAmount));
                break;
            case IRON:
                station.setIronStored(Math.max(0, station.getIronStored() - collectedAmount));
                break;
            case STEEL:
                station.setSteelStored(Math.max(0, station.getSteelStored() - collectedAmount));
                break;
        }
    }

    private void updateStationCapacity(Station station) {
        // Calculate total stored cargo across all types
        int totalStored = station.getPeopleStored() + station.getMailStored() +
                station.getCoalStored() + station.getIronStored() + station.getSteelStored();
        station.setCapacityOwned(totalStored);
    }

    private void updateCollectionResult(CargoCollectionResult result, Carriage.Type cargoType, int amount) {
        switch (cargoType) {
            case PEOPLE: result.people += amount; break;
            case MAIL: result.mail += amount; break;
            case COAL: result.coal += amount; break;
            case IRON: result.iron += amount; break;
            case STEEL: result.steel += amount; break;
        }
    }

    private boolean hasHistoricalRestriction(Scenario scenario, String restriction) {
        return scenario.getHistoricalRestrictions().stream()
                .anyMatch(r -> r.equalsIgnoreCase(restriction));
    }

    private boolean isTrainFull(List<Carriage> carriages) {
        if (carriages == null || carriages.isEmpty()) {
            return true;
        }

        boolean hasAvailableSpace = false;

        for (int i = 0; i < carriages.size(); i++) {
            Carriage c = carriages.get(i);

            if (c.getCapacityOwned() < c.getCapacity()) {
                hasAvailableSpace = true;
            }
        }

        return !hasAvailableSpace;
    }

    private static class CargoCollectionResult {
        int people = 0;
        int mail = 0;
        int coal = 0;
        int iron = 0;
        int steel = 0;

        int getTotalCargo() {
            return people + mail + steel;
        }

        boolean hasIronAndCoal() {
            return iron > 0 && coal > 0;
        }
    }

    public List<Industry> getIndustriesForMap(Map map) {
        return industryRepo.getIndustries().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    public List<City> getCitiesForMap(Map map) {
        return cityRepo.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    public List<Station> getUserStations(Map map, Scenario scenario) {
        if (currentUser == null) return new ArrayList<>();

        return stationrepo.getStations().stream()
                .filter(st -> st.getMapName().equals(map.getMapName()) &&
                        st.getOwnerPlayer().equals(currentUser.getEmail()) &&
                        st.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    // Time management methods
    public Runnable passTime(Scenario selectedScenario, AtomicReference<AtomicIntegerArray> timePeriod,
                             AtomicBoolean running, List<MenuItem> options, ScheduledExecutorService scheduler,
                             AtomicBoolean gameOver, Budget budget, Simulation sim) {
        return () -> {
            if (!running.get()) return;

            int nextYear = timePeriod.get().addAndGet(0, 1);
            sim.setCurrentYear(nextYear);

            if (nextYear < timePeriod.get().get(1)) {
                Map map = mapRepo.getMaps().stream().filter(m-> m.getMapName().equals(selectedScenario.getSelectedMap())).findFirst().orElse(null);
                handleYearProgression(nextYear, budget, selectedScenario, map,
                        timePeriod, scheduler, running, options, gameOver, sim);

            } else {
                System.out.println("\n Final year was reached");
                handleGameEnd(selectedScenario, timePeriod, scheduler, running, options, gameOver, sim);
            }
        };
    }

    private void handleYearProgression(int year, Budget budget, Scenario selectedScenario, Map map,
                                       AtomicReference<AtomicIntegerArray> timePeriod,
                                       ScheduledExecutorService scheduler, AtomicBoolean running,
                                       List<MenuItem> options, AtomicBoolean gameOver, Simulation sim) {
        int yearSummary = year - 1;
        int locomotiveMaintenanceCost = calculateLocomotiveMaintenanceCost();
        long ownedTracksAmount = railwayRepo.countByScenarioMapAndOwner(selectedScenario, map, currentUser.getEmail());
        int trackMaintenanceCost = Math.toIntExact(AMOUNT_PER_LINE * ownedTracksAmount);
        yearlyTotalCost = yearlyFuelCost + trackMaintenanceCost + locomotiveMaintenanceCost;

        Budget money = budgetRepo.getUserBudgetFromScenarioInMap(selectedScenario, map, currentUser).orElse(null);
        int currentBudget = money != null ? money.getBudget() : budget.getBudget();

        if (currentBudget >= yearlyTotalCost) {
            System.out.println("\n=== YEAR " + yearSummary + " SUMMARY ===");
            payLocomotiveMaintenance(budget);
            budget.subtractBudget(yearlyFuelCost);
            payTrackMaintenance(selectedScenario, map, budget);
            yearlyMaintenanceCost = locomotiveMaintenanceCost;
            yearlyTrackMaintenanceCost = trackMaintenanceCost;

            displayYearlyStatistics();

            demandManager.updateYearlyDemand(selectedScenario);

            announceNewLocomotives(year);
            removeOutdatedLocomotives(year);
            resetYearlyCounters();
            System.out.println("\n===========================");
            System.out.println("The year is now " + year);

            System.out.print("Type your option: ");
        } else {
            System.out.println("\n=== INSUFFICIENT BUDGET ===");
            System.out.printf("Required budget: $%d%n", yearlyTotalCost);
            System.out.printf("Available budget: $%d%n", currentBudget);
            System.out.println("Not enough budget to pay your yearly expenses");
            handleGameEnd(selectedScenario, timePeriod, scheduler, running, options, gameOver, sim);
        }

        yearlyTotalCost = 0;
    }

    private void payTrackMaintenance(Scenario selectedScenario, Map map,Budget budget) {
        long ownedTracksAmount = railwayRepo.countByScenarioMapAndOwner(selectedScenario, map, currentUser.getEmail());
        int moneySpent = Math.toIntExact(AMOUNT_PER_LINE * ownedTracksAmount);
        yearlyTrackMaintenanceCost = moneySpent;
        budget.subtractBudget(moneySpent);
    }

    private void displayYearlyStatistics() {
        System.out.println("\n--- Financial Summary ---");
        System.out.printf("Total Earnings from Routes: $%d%n", yearlyEarnings);
        System.out.printf("Locomotive Maintenance Costs: $%d%n", yearlyMaintenanceCost);
        System.out.printf("Track Maintenance Costs: $%d%n", yearlyTrackMaintenanceCost);
        System.out.printf("Fuel Costs: $%d%n", yearlyFuelCost);
        System.out.println("\n------------------------");
    }

    private void resetYearlyCounters() {
        yearlyEarnings = 0;
        yearlyMaintenanceCost = 0;
        yearlyFuelCost = 0;
        yearlyTrackMaintenanceCost = 0;

    }

    private void announceNewLocomotives(int year) {
        Repositories.getInstance().getLocomotiveRepository().getLocomotives().stream()
                .filter(l -> l.getStartYearOfOperation() == year)
                .forEach(l -> System.out.println("Locomotive " + l.getModelName() + " has become available."));
    }

    private void handleGameEnd(Scenario scenario, AtomicReference<AtomicIntegerArray> timePeriod,
                               ScheduledExecutorService scheduler, AtomicBoolean running,
                               List<MenuItem> options, AtomicBoolean gameOver, Simulation sim) {
        scheduler.shutdownNow();
        running.set(false);

        int resetYear = scenario.getTimeRestrictions()[0];
        sim.setCurrentYear(resetYear);
        timePeriod.set(new AtomicIntegerArray(scenario.getTimeRestrictions()));

        gameOver.set(true);
        System.out.print("\nGame Over. Please insert 0 to return to menu: ");
    }

    private int calculateLocomotiveMaintenanceCost() {
        List<Locomotive> allLocomotives = currentUser.getLocomotives();
        int totalMaintenanceCost = 0;

        for (Locomotive locomotive : allLocomotives) {
            totalMaintenanceCost += locomotive.getMaintenanceCost();
        }

        return totalMaintenanceCost;
    }

    public void generateCargo(Map selectedMap, Scenario selectedScenario,
                              AtomicReference<AtomicIntegerArray> timePeriod, AtomicBoolean running,
                              ScheduledExecutorService generationScheduler) {
        if (running.get() && timePeriod.get().get(0) <= timePeriod.get().get(1)) {
            addCargoToStation(selectedMap, selectedScenario);
        }
        if (timePeriod.get().get(0) > timePeriod.get().get(1)) {
            generationScheduler.shutdownNow();
        }
    }

    public void addCargoToStation(Map selectedMap, Scenario selectedScenario) {
        String mapName = selectedMap.getMapName();
        List<Station> stations = getUserStationsForMap(mapName, selectedScenario);
        Set<Station> maxCapacityStations = new HashSet<>();

        addCityCargo(stations, selectedScenario, maxCapacityStations);
        addIndustryCargo(stations, selectedScenario, maxCapacityStations, mapName);
    }

    private List<Station> getUserStationsForMap(String mapName, Scenario scenario) {
        return stationrepo.getStations().stream()
                .filter(s -> s.getMapName().equals(mapName) &&
                        s.getScenarioName().equals(scenario.getScenarioName()) &&
                        s.getOwnerPlayer().equals(currentUser.getEmail()))
                .collect(Collectors.toList());
    }

    public void addCityCargo(List<Station> stations, Scenario selectedScenario, Set<Station> maxCapacityStations) {
        cityRepo.getCities().forEach(city -> {
            List<Station> cityStations = findStationsForCity(stations, city);
            if (!cityStations.isEmpty()) {
                double cargoMultiplier = selectedScenario.getProductionRate();
                int totalCargo = (int) (city.getTotalGeneratedCargo() * cargoMultiplier);

                cityStations.forEach(station -> addCityCargoToStation(station, totalCargo, maxCapacityStations));
            }
        });
    }

    private List<Station> findStationsForCity(List<Station> stations, City city) {
        return stations.stream()
                .filter(s -> s.getName().contains(city.getName()))
                .collect(Collectors.toList());
    }

    private void addCityCargoToStation(Station station, int totalCargo, Set<Station> maxCapacityStations) {
        int currentCapacity = station.getCapacityOwned();
        int maxCapacity     = station.getCapacity();
        if (currentCapacity >= maxCapacity) return;

        int actualCargoAdded = Math.min(totalCargo, maxCapacity - currentCapacity);
        if (actualCargoAdded <= 0) return;

        int newTotalCapacity = currentCapacity + actualCargoAdded;
        int targetTotalPeople = Math.round(newTotalCapacity * 0.7f);
        int peopleToAdd = targetTotalPeople - station.getPeopleStored();
        int mailToAdd   = actualCargoAdded - peopleToAdd;

        station.setPeopleStored(station.getPeopleStored() + peopleToAdd);
        station.setMailStored(station.getMailStored() + mailToAdd);
        station.setCapacityOwned(newTotalCapacity);

        System.out.printf("\nStored %d cargo for station %s (%d people, %d mail) (Total: %d/%d). Type your option: ",
                actualCargoAdded, station.getName(), peopleToAdd, mailToAdd,
                station.getCapacityOwned(), maxCapacity);

        if (newTotalCapacity >= maxCapacity && !maxCapacityStations.contains(station)) {
            System.out.printf("\nStation %s has reached maximum capacity (%d/%d)!",
                    station.getName(), newTotalCapacity, maxCapacity);
            maxCapacityStations.add(station);
        }
    }

    public void addIndustryCargo(List<Station> stations, Scenario selectedScenario, Set<Station> maxCapacityStations, String mapName) {
        if(selectedScenario.getPortBehavior() == PORT_BEHAVIOUR) {
            if (stations.isEmpty()) {
                System.out.println("No stations available for cargo generation");
                return;
            }

            List<Industry> mapIndustries = industryRepo.getIndustries().stream()
                    .filter(industry -> industry.getMapName().equals(mapName))
                    .collect(Collectors.toList());

            for (Industry industry : mapIndustries) {
                List<Station> overlappingStations = stations.stream()
                        .filter(station -> hasStationIndustryOverlap(station, industry))
                        .collect(Collectors.toList());

                if (!overlappingStations.isEmpty()) {
                    int cargoPerIndustry = (int) (getCargoPerIndustry(industry) * selectedScenario.getProductionRate());
                    if (cargoPerIndustry > 0) {
                        Station targetStation = overlappingStations.get(0);

                        int actualAddedCargo = addCargoToStation(targetStation, cargoPerIndustry, maxCapacityStations);
                        distributeIndustryCargo(targetStation, industry, actualAddedCargo);

                    }
                }
            }
        }
    }

    private boolean hasStationIndustryOverlap(Station station, Industry industry) {
        double industryRadius = (double) industry.getArea() /2;
        int industryX = industry.getLocation().getX();
        int industryY = industry.getLocation().getY();
        StationBounds bounds = getStationBounds(station);

        return !(bounds.maxX < industryX - industryRadius ||
                bounds.minX > industryX + industryRadius ||
                bounds.maxY < industryY - industryRadius ||
                bounds.minY > industryY + industryRadius);
    }

    private StationBounds getStationBounds(Station station) {
        int w, h, minX, minY;

        switch (station.getType()) {
            case DEPOT:
                w = h = 3;
                minX = station.getX() - 1;
                minY = station.getY() - 1;
                break;
            case TERMINAL:
                w = h = 5;
                minX = station.getX() - 2;
                minY = station.getY() - 2;
                break;
            case STATION:
                w = h = 4;
                minX = station.getX() - 2; // Fixed: center the 4x4 area
                minY = station.getY() - 2; // Fixed: center the 4x4 area
                break;
            default:
                return new StationBounds(station.getX(), station.getY(), station.getX(), station.getY());
        }

        return new StationBounds(minX, minY, minX + w - 1, minY + h - 1);
    }

    private static class StationBounds {
        final int minX, minY, maxX, maxY;

        StationBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    private void distributeIndustryCargo(Station station, Industry industry, int actualAddedCargo) {
        switch (industry.getType()) {
            case IRON_MINE:
                station.setIronStored(station.getIronStored() + actualAddedCargo);
                break;
            case COAL_MINE:
                station.setCoalStored(station.getCoalStored() + actualAddedCargo);
                break;
            case STEEL_MILL:
                if (actualAddedCargo > 0) {
                    station.setSteelStored(station.getSteelStored() + actualAddedCargo);
                    station.setIronStored(station.getIronStored() - station.getSteelStored());
                    station.setCoalStored(station.getCoalStored() - station.getSteelStored());
                }
                break;
        }
    }

    private int getCargoPerIndustry(Industry ind) {
        switch (ind.getType()) {
            case IRON_MINE: return ind.getGeneratedIron();
            case COAL_MINE: return ind.getGeneratedCoal();
            case STEEL_MILL:
                if (ind.getStoredCoal() > 0 && ind.getStoredIron() > 0) return Math.min(ind.getStoredCoal(), ind.getStoredIron());
                return 0;
            default: return 0;
        }
    }

    private int addCargoToStation(Station station, int cargoToAdd, Set<Station> maxCapacityStations) {
        int currentCapacity = station.getCapacityOwned();
        int maxCapacity = station.getCapacity();

        if (currentCapacity >= maxCapacity) return 0;

        int actualCargoAdded = Math.min(cargoToAdd, maxCapacity - currentCapacity);
        station.setCapacityOwned(currentCapacity + actualCargoAdded);

        System.out.printf("\nStored %d cargo for station %s (Total: %d/%d). Type your option: ",
                actualCargoAdded, station.getName(), station.getCapacityOwned(), maxCapacity);

        if (station.getCapacityOwned() >= maxCapacity && !maxCapacityStations.contains(station)) {
            System.out.printf("\nStation %s has reached maximum capacity (%d/%d)!",
                    station.getName(), station.getCapacityOwned(), maxCapacity);
            maxCapacityStations.add(station);
        }

        return actualCargoAdded;
    }

    public void payLocomotiveMaintenance(Budget budget) {
        List<Locomotive> allLocomotives = currentUser.getLocomotives();
        List<Locomotive> maintainedLocomotives = new ArrayList<>();

        for (Locomotive loc : allLocomotives) {
            payMaintenanceAndFuel(loc, budget);
            maintainedLocomotives.add(loc);
        }
    }


    private void payMaintenanceAndFuel(Locomotive locomotive, Budget budget) {
        budget.subtractBudget(locomotive.getMaintenanceCost());
        locomotive.setAvailability(locomotive.getFuelCost() <= budget.getBudget());

        if (locomotive.getAvailability()) {
            budget.subtractBudget(yearlyFuelCost / KMS_FUEL);
        }
    }

    public void removeOutdatedLocomotives(int year) {
        List<Locomotive> activeLocomotives = currentUser.getLocomotives().stream()
                .filter(locomotive -> {
                    if (locomotive.getEndYearOfOperation() <= year) {
                        System.out.println("\n" + locomotive.getModelName() +
                                " removed from user's locomotives since the end of its operation is " + year);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        currentUser.setLocomotives(activeLocomotives);
    }

    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
    }

    public Scenario getScenario() {
        List<Scenario> allScenarios = scenarioRepo.getScenarios();

        if (allScenarios.isEmpty()) {
            System.out.println("No scenarios available.");
            return null;
        }

        List<String> displayList = allScenarios.stream()
                .map(s -> s.getScenarioName() + " - " + s.getSelectedMap())
                .collect(Collectors.toList());

        int idx = Utils.showAndSelectIndex(displayList, "Select the scenario you want to play in:");

        return idx == -1 ? null : allScenarios.get(idx);
    }


    public Budget checkOrCreateBudget(Map selectedMap, Scenario selectedScenario) {
        Budget budget = null;

        if (currentUser != null) {
            Optional<Budget> existingBudget = budgetRepo.getUserBudgetFromScenarioInMap(
                    selectedScenario, selectedMap, currentUser);

            if (existingBudget.isPresent()) {
                budget = existingBudget.get();
            } else {
                budget = new Budget(1000, selectedMap, selectedScenario, currentUser);
                budgetRepo.createBudget(budget);
            }
        }
        return budget;
    }

    public void updateSimulation(Map map, Scenario scenario, int newYear, Budget newBudget) {
        simulationRepo.updateSimulation(map, scenario, currentUser, newYear, newBudget);
    }

    public void resetGame(Map selectedMap, Scenario selectedScenario, Budget budget) {
        List<Locomotive> l = new ArrayList<>();
        currentUser.setLocomotives(l);
        budget.setBudget(1000);
        stationrepo.removeStationsByMapScenarioAndOwner(
                selectedMap.getMapName(),
                selectedScenario.getScenarioName(),
                currentUser.getEmail()
        );
    }

    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return simulationRepo.findBy(map, scenario, currentUser);
    }
}