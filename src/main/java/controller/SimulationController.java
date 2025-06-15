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

/**
 * The SimulationController class provides functionalities to manage and execute the simulation of train journeys,
 * cargo distribution, yearly progressions, and related operations within a simulation environment.
 * It manages various repositories and ensures accurate handling of scenarios, routes, and industry interactions.
 *
 * Fields:
 * - Simulation constants including resource values (PEOPLE_VALUE, MAIL_VALUE, etc.).
 * - Yearly financial metrics for the simulation (yearlyEarnings, yearlyMaintenanceCost, yearlyFuelCost, etc.).
 * - Lists tracking simulation state such as active journeys, waiting trains, trains preserving cargo, etc.
 * - Current user and various repository references (simulationRepo, mapRepo, industryRepo, etc.).
 * - Demand manager for handling simulations of demands across the environment.
 *
 * Methods:
 * - Public methods include route execution, cargo handling, locomotion maintenance, and simulation resetting.
 * - A wide variety of private helper methods exist for detailed operations such as checking route validity,
 *   updating station capacity, managing yearly progression, validating restrictions, and interacting with industries.
 * - Train journey handling includes cargo loading and unloading, calculating arrival earnings, managing route distances,
 *   and ensuring proper cargo distribution between entities.
 * - Yearly time progression handles maintenance costs, locomotive updates, and game-end processes.
 * - Data retrieval methods for accessing industries, cities, stations, and user-related details for simulations.
 *
 * Responsibilities:
 * - Execute train routes and manage the entire lifecycle of these journeys based on selected maps and scenarios.
 * - Calculate costs, earnings, and budget implications for each year of the simulation.
 * - Track cargo distributions across stations, industries, and steel mills to manage simulation outcomes.
 * - Monitor and enforce simulation rules related to locomotives, railways, and historical restrictions.
 * - Reset and update simulation parameters to ensure a seamless user experience across runs.
 *
 * Superclass:
 * - java.lang.Object
 */
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

    /**
     * Represents a train journey that manages the route, departure year,
     * estimated arrival year, and completion status of a train during its journey.
     */
    private static class TrainJourney {
        private final Route route;
        private final int departureYear;
        private final int estimatedArrivalYear;
        private boolean completed;

        /**
         * Constructs a new TrainJourney object, initializing with a route, the current simulation year,
         * and calculating the estimated arrival year based on the given current year and the route's details.
         *
         * @param route the route associated with the train journey, containing information such as the stations and operations along the journey
         * @param currentSimulationYear the current year in the simulation, used as the departure year for this train journey
         */
        public TrainJourney(Route route, int currentSimulationYear) {
            this.route = route;
            this.departureYear = currentSimulationYear;
            this.completed = false;
            this.estimatedArrivalYear = calculateEstimatedArrivalYear(currentSimulationYear) + 1;
        }

        /**
         * Calculates the estimated year of arrival for a train journey based on the current year,
         * the route's details, and the train's locomotive specifications.
         *
         * The calculation considers the total distance of the route, the locomotive's top speed,
         * and a degradation factor impacting the locomotive's effective speed. If the route
         * does not have sufficient points, or if the train or its locomotive is not defined,
         * the method returns the current year as a fallback.
         *
         * @param currentYear the current simulation year used as the starting point for the journey calculation
         * @return the estimated arrival year of the train journey based on the route and locomotive details
         */
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

        /**
         * Calculates the speed degradation of a train based on the number of carriages attached to it.
         *
         * The method first checks if the train or its carriages are null or empty. If either of these
         * conditions is true, the method returns a degradation of 0.0. Otherwise, it calculates the
         * degradation factor by multiplying the number of carriages by a constant (0.05). The result
         * is capped at a maximum value of 0.30 to ensure the degradation does not exceed a predefined limit.
         *
         * @return the calculated speed degradation as a double value. Returns 0.0 if the train or
         * its carriages are not defined, or the list of carriages is empty.
         */
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

        /**
         * Finds a railway line connecting the two specified stations, owned by the given owner.
         * The method searches for a railway line where the starting and destination stations match
         * the provided stations in either order, and the line's owner matches the provided owner.
         * If no matching railway line is found, the method returns null.
         *
         * @param station1 the first station to be matched in the railway line
         * @param station2 the second station to be matched in the railway line
         * @param owner the email address of the owner of the railway line
         * @return the matching RailwayLine instance if found, otherwise null
         */
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

        /**
         * Retrieves the route associated with the current train journey.
         *
         */
        public Route getRoute() { return route; }

        /**
         * Retrieves the departure year of the train journey.
         *
         * @return the departure year as an integer.
         */
        public int getDepartureYear() { return departureYear; }

        /**
         * Retrieves the estimated year of arrival for the train journey.
         *
         * @return the estimated arrival year as an integer.
         */
        public int getEstimatedArrivalYear() { return estimatedArrivalYear; }

        /**
         * Checks whether the train journey is completed.
         *
         * @return true if the train journey is completed, false otherwise
         */
        public boolean isCompleted() { return completed; }

        /**
         * Updates the completion status of the train journey.
         *
         * @param completed a boolean value indicating whether the train journey is completed (true) or not (false)
         */
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    /**
     * The DemandManager class is responsible for managing the yearly demand for different types of cargo including people, mail, coal, iron, and steel.
     * It tracks delivery quantities, adjusts demand levels based on delivery performance, and incorporates modifications from historical scenarios.
     */
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

        /**
         * Retrieves the current*/
        public int getPeopleDemand() { return peopleDemand; }

        /**
         * Retrieves the current demand for mail.
         *
         * @return the current mail demand as an integer
         */
        public int getMailDemand() { return mailDemand; }

        /**
         * Retrieves the current demand for coal.
         *
         * @return the current coal demand as an integer
         */
        public int getCoalDemand() { return coalDemand; }

        /**
         * Retrieves the current demand for iron.
         *
         * @return the current iron demand as an integer
         */
        public int getIronDemand() { return ironDemand; }

        /**
         * Retrieves the current steel demand.
         *
         * @return the current steel demand as an integer
         */
        public int getSteelDemand() { return steelDemand; }

        /**
         * Records the delivery of a specific type of cargo and updates the yearly delivery count.
         *
         * @param cargoType the type of cargo being delivered, categorized as one of the defined types in {@link Carriage.Type}
         * @param amount the amount of cargo delivered; must be a positive integer
         */
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

        /**
         * Updates the yearly demand for different types of commodities and applies scenario-specific modifications.
         * The method recalculates demand values based on the yearly deliveries and adjusts them according to the rules defined
         * in the scenario. It also resets the yearly delivery counts and displays a summary of the demand update.
         *
         * @param scenario the scenario used to determine modifications to demand based on historical restrictions
         */
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

        /**
         * Calculates the new demand for a resource based on the current demand, yearly delivered amount,
         * and applies some randomness within defined constraints.
         *
         * @param currentDemand the current demand for the resource
         * @param yearlyDelivered the total amount of the resource delivered in the year
         * @return the updated demand after applying adjustments and randomness
         */
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

        /**
         * Applies scenario-specific modifications to the demand values, adjusting them based
         * on various historical restrictions defined in the given scenario.
         *
         * The method checks for specific historical restrictions such as "industrial revolution",
         * "vaccination campaign", "economic depression", and "war time", and modifies the
         * associated demand variables accordingly. These modifications are constrained
         * by the minimum and maximum demand limits.
         *
         * @param scenario the scenario containing historical restrictions that dictate
         *                 adjustments to the demand values
         */
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

        /**
         * Checks if a specific historical restriction is present in the given scenario.
         *
         * @param scenario the scenario to check for the specified historical restriction
         * @param restriction the historical restriction to search for
         * @return true if the scenario contains the specified historical restriction, false otherwise
         */
        private boolean hasHistoricalRestriction(Scenario scenario, String restriction) {
            return scenario.getHistoricalRestrictions().stream()
                    .anyMatch(r -> r.equalsIgnoreCase(restriction));
        }

        /**
         * Resets the yearly delivery counts for all tracked commodities to zero.
         * This method is typically invoked at the end of a year to clear the accumulated
         * delivery statistics in preparation for a new year's data.
         */
        private void resetYearlyDeliveries() {
            yearlyPeopleDelivered = 0;
            yearlyMailDelivered = 0;
            yearlyCoalDelivered = 0;
            yearlyIronDelivered = 0;
            yearlySteelDelivered = 0;
        }

        /**
         * Displays a summary of the updated yearly demand for various commodities.
         *
         * This method outputs the updated demand levels for people, mail, coal, iron,
         * and steel for the new year. It also serves as a simple log to indicate that
         * the demand levels have been refreshed. This is typically called after
         * recalculating the yearly demand values.
         */
        private void displayDemandUpdate() {
            System.out.println("\n[Yearly Demand Update] Demand levels updated for the new year.");
            System.out.printf("People: %d, Mail: %d, Coal: %d, Iron: %d, Steel: %d%n",
                    peopleDemand, mailDemand, coalDemand, ironDemand, steelDemand);
        }
    }

    private final DemandManager demandManager = new DemandManager();

    /**
     * Executes the routes for the current user in the simulation.
     * This method processes user-defined routes, starts train journeys,
     * and checks arriving trains based on the provided parameters.
     *
     * @param selectedMap           the map currently selected for the simulation
     * @param selectedScenario      the scenario currently selected for the simulation
     * @param budget                the budget object managing the user's funds
     * @param running               an atomic boolean indicating if the simulation is running
     * @param currentSimulationYear the current year in the simulation
     * @return a Runnable to execute route-related operations
     */
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

    /**
     * Validates the given Route to ensure it meets the necessary criteria for execution.
     * Checks for the presence of a train, sufficient points along the route,
     * and valid connections between stations.
     *
     * @param route the Route object to be validated
     * @return true if the route is valid; false otherwise
     */
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

    /**
     * Starts a train journey for the given route and simulation year.
     * This method initializes a new TrainJourney object, generates
     * a unique route ID, and adds the journey to the collection of active journeys.
     * It also prints departure information, including the starting station,
     * departure year, and the estimated arrival year.
     *
     * @param route                the Route object representing the path of the train journey
     * @param currentSimulationYear the current year in the simulation for the journey to start
     */
    private void startTrainJourney(Route route, int currentSimulationYear) {
        TrainJourney journey = new TrainJourney(route, currentSimulationYear);
        String routeId = generateRouteId(route);

        activeJourneys.put(routeId, journey);

        System.out.printf("\n[Train Departure] Train departed from %s in year %d. Estimated arrival: year %d. Type your option: ",
                route.getPoints().get(0).getStation().getName(),
                journey.getDepartureYear(),
                journey.getEstimatedArrivalYear());
    }

    /**
     * Checks for and processes trains that have arrived at their destination in the simulation.
     * This method evaluates each active train journey to determine if it has arrived,
     * updates their status, and removes completed journeys from the list of active journeys.
     *
     * @param scenario             the scenario currently being used in the simulation
     * @param budget               the budget object managing the user's funds
     * @param selectedMap          the map currently selected for the simulation
     * @param currentSimulationYear the current year in the simulation
     */
    private void checkArrivingTrains(Scenario scenario, Budget budget, Map selectedMap, int currentSimulationYear) {
        List<String> completedJourneys = new ArrayList<>();

        for (java.util.Map.Entry<String, TrainJourney> entry : activeJourneys.entrySet()) {
            TrainJourney journey = entry.getValue();
            String routeId = entry.getKey();

            // If train is waiting at a station, try to collect more cargo and check if it can depart
            if (waitingTrainsAtStation.containsKey(routeId)) {
                // Continue collecting cargo while waiting
                continueCollectingAtWaitingStation(journey.getRoute(), routeId);

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

    /**
     * Continues collecting cargo at a station where a train is currently waiting.
     * This method is called each simulation cycle to allow trains to gradually
     * collect cargo until their departure requirements are met.
     *
     * @param route the route of the waiting train
     * @param routeId the unique identifier of the route
     */
    private void continueCollectingAtWaitingStation(Route route, String routeId) {
        int stationIndex = waitingTrainsAtStation.get(routeId);
        PointOfRoute stationPoint = route.getPoints().get(stationIndex);
        Station station = stationPoint.getStation();
        List<Carriage.Type> requestedTypes = stationPoint.getCargoTypes();

        // Only collect if station has available cargo
        if (station.getCapacityOwned() > 0) {
            collectAvailableCargoForWaiting(route, station, requestedTypes);
            System.out.printf("\n[Continuing Collection] Train continuing to collect at %s. Station capacity: %d. Type your option: ",
                    station.getName(), station.getCapacityOwned());
        }
    }

    /**
     * Displays information about the completion of a train route, including details about
     * the year of arrival, cargo collected, and total earnings.
     *
     * @param result       the CargoCollectionResult object containing the quantities
     *                     of each type of cargo collected during the route
     * @param totalEarnings the total earnings generated from the completed route
     * @param journey      the TrainJourney object representing the details of the journey,
     *                     such as the departure year
     * @param currentYear  the year in which the train completed its journey
     */
    private void displayRouteCompleteWithYear(CargoCollectionResult result, int totalEarnings, TrainJourney journey, int currentYear) {
        System.out.printf("\n[Train Arrival] Train arrived in year %d (departed year %d). Total Cargo: %d (People: %d, Mail: %d, Iron: %d, Coal: %d, Steel: %d) - Earned: %d. Type your option: ",
                currentYear, journey.getDepartureYear(), result.getTotalCargo(), result.people, result.mail, result.iron, result.coal, result.steel, totalEarnings);
    }

    /**
     * Calculates the total earnings based on the transported cargo and specific conditions, such as route distance,
     * demand multipliers, and scenario constraints.
     *
     * @param result The cargo collection results containing quantities of people, mail, coal, iron, and steel.
     * @param scenario The scenario defining specific constraints or multipliers, e.g., a vaccination campaign.
     * @param route The route taken, used to calculate the distance multiplier for earnings.
     * @param selectedMap The map data used to determine route details and related constraints.
     * @param peopleEarningMultiplier An atomic reference to the multiplier applied to earnings derived from transporting people.
     * @return The total calculated earnings as an integer value.
     */
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

    /**
     * Calculates the total distance of a given route based on the stations it includes.
     * The calculation includes summing up distances between consecutive stations and adjusts
     * the result using the scale property of the provided map.
     *
     * @param route the route for which the distance is to be calculated. Must include a list of points containing stations.
     * @param selectedMap the map which provides a scale to adjust the total distance calculated.
     * @return the total distance of the route adjusted by the map's scale. Returns 0.0 if the route has fewer than two points.
     */
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

    /**
     * Generates a unique identifier for a given Route object by combining
     * certain properties of the Route such as map name, scenario name, player email,
     * points hash code, and train hash code (if available).
     *
     * @param route The Route object for which the unique identifier is to be generated.
     * @return A string representing the unique identifier for the given Route object.
     */
    private String generateRouteId(Route route) {
        return route.getMapName() + "_" + route.getScenarioName() + "_" + route.getPlayerEmail() + "_" +
                route.getPoints().hashCode() + "_" + (route.getTrain() != null ? route.getTrain().hashCode() : 0);
    }

    /**
     * Retrieves a list of routes associated with the current user based on the provided map and scenario.
     *
     * @param map the map object containing details about the map being used
     * @param scenario the scenario object containing details about the scenario being used
     * @return a list of routes linked to the current user, filtered by map and scenario
     */
    private List<Route> getUserRoutes(Map map, Scenario scenario) {
        return routeRepo.getRoutes().stream()
                .filter(r -> r.getPlayerEmail().equals(currentUser.getEmail())
                        && r.getMapName().equals(map.getMapName())
                        && r.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the fuel cost required for a train to travel along a given route.
     *
     * @param route The route object containing the sequence of points (stations) to be traversed.
     *              The route must have at least two points for the calculation to proceed.
     * @param selectedMap A map object that determines the scale of the route.
     *                    The scale is used to adjust the total distance for accurate cost computation.
     * @return The calculated fuel cost for the entire route based on the train's locomotive fuel cost,
     *         the total distance of the route, and the map scale. Returns 0 if the route is invalid,
     *         or if the train or locomotive is not set.
     */
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

    /**
     * Finds a railway line connecting two specified stations owned by a particular owner.
     *
     * @param station1 the starting station of the railway line
     * @param station2 the ending station of the railway line
     * @param owner the owner of the railway line
     * @return the RailwayLine object connecting the specified stations with the given owner,
     *         or null if no such railway line exists
     */
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

    /**
     * Resets the capacity of all carriages in the provided list to zero.
     *
     * @param carriages the list of Carriage objects whose capacities will be reset
     */
    private void resetCarriageCapacity(List<Carriage> carriages) {
        carriages.forEach(c -> c.setCapacityOwned(0));
    }

    /**
     * Handles the delivery of cargo to steel mills based on the specified route,
     * ensuring that the required resources are available in the provided result data.
     *
     * @param route The route specifying the map and other relevant routing information.
     * @param carriages A list of carriages used to transport cargo to the steel mills.
     * @param result The collection result containing the cargo payload to be delivered and their availability.
     */
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

    /**
     * Delivers iron and coal cargo from the given list of carriages to the specified steel mill.
     * Updates the cargo collection result and steel mill's stored resources accordingly.
     *
     * @param carriages the list of carriages containing cargo to be delivered
     * @param result an instance of CargoCollectionResult to track the remaining iron and coal
     * @param steelMill the steel mill where the cargo is delivered
     */
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

    /**
     * Removes cargo of a specified type from the train carriages and calculates the total capacity removed.
     *
     * @param carriages a list of carriages from which the cargo will be removed
     * @param cargoType the type of cargo to be removed from the carriages
     * @return the total capacity of the removed cargo
     */
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

    /**
     * Collects cargo from a specified station based on the requested cargo types and updates the result and station data.
     *
     * @param route the route associated with the cargo collection
     * @param station the station from which cargo is to be collected
     * @param requestedTypes the list of cargo types requested to be collected
     * @param result the result object to store details of the cargo collection
     */
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

    /**
     * Processes cargo at a single station based on the cargo mode and specified capacities.
     * Determines the target amount of cargo to be loaded or unloaded depending on the station's
     * cargo mode, the train's current cargo load, and the available cargo at the station.
     *
     * @param route the train route, which contains the train and associated details
     * @param stationPoint the specific point on the route where the processing is taking place
     * @param maxCargoTypeCapacity the maximum capacity of the specified cargo type that the train can hold
     * @param availableCargo the amount of the specified cargo type available at the station
     * @param cargoType the type of cargo to be processed
     * @return the amount of cargo that can be processed (either loaded or taken into account) at the station
     */
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

    /**
     * Calculates the required cargo capacity based on the specified cargo mode.
     *
     * @param cargoMode the mode of cargo operation, which determines the calculation method
     * @param maxCargoTypeCapacity the maximum cargo capacity for the given type
     * @param availableCargo the current available cargo capacity
     * @return the calculated cargo capacity based on the specified cargo mode
     * @throws IllegalArgumentException if an unknown cargo mode is provided
     */
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

    /**
     * Determines if a train can depart from a station based on the cargo mode, available cargo,
     * and the train's current cargo state. Only collects cargo if requirements can be met.
     *
     * @param route the route that the train is following, containing the station information and train details.
     * @param stationIndex the index of the station point in the route from which the train is attempting to depart.
     * @return true if the train can depart from the station according to the station's cargo mode and requirements, false otherwise.
     */
    private boolean canTrainDepartFromStation(Route route, int stationIndex) {
        PointOfRoute stationPoint = route.getPoints().get(stationIndex);
        Station station = stationPoint.getStation();
        CargoMode cargoMode = stationPoint.getCargoMode();
        List<Carriage.Type> requestedTypes = stationPoint.getCargoTypes();
        Train train = route.getTrain();

        switch (cargoMode) {
            case AVAILABLE:
                // For AVAILABLE mode, always collect what's available and allow departure
                collectAvailableCargoForWaiting(route, station, requestedTypes);
                return true;

            case HALF:
                // Check if station has enough cargo to meet HALF requirements
                boolean canMeetHalfRequirements = requestedTypes.stream()
                        .allMatch(type -> {
                            int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                            int halfCapacity = (int) Math.ceil(maxCapacityForType * 0.5);
                            int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                            int needed = halfCapacity - currentCargoInTrain;
                            int available = getAvailableCargoAtStation(station, type);
                            return needed <= 0 || available >= needed; // Already has enough OR station has enough
                        });

                if (canMeetHalfRequirements) {
                    collectAvailableCargoForWaiting(route, station, requestedTypes);

                    // Verify the train now meets HALF requirements
                    return requestedTypes.stream()
                            .allMatch(type -> {
                                int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                                int halfCapacity = (int) Math.ceil(maxCapacityForType * 0.5);
                                int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                                return currentCargoInTrain >= halfCapacity;
                            });
                }
                return false; // Can't meet requirements, don't collect cargo

            case FULL:
                // Check if station has enough cargo to meet FULL requirements
                boolean canMeetFullRequirements = requestedTypes.stream()
                        .allMatch(type -> {
                            int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                            int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                            int needed = maxCapacityForType - currentCargoInTrain;
                            int available = getAvailableCargoAtStation(station, type);
                            return needed <= 0 || available >= needed; // Already full OR station has enough
                        });

                if (canMeetFullRequirements) {
                    collectAvailableCargoForWaiting(route, station, requestedTypes);

                    // Verify the train now meets FULL requirements
                    return requestedTypes.stream()
                            .allMatch(type -> {
                                int maxCapacityForType = calculateCargoTypeCapacity(train, type);
                                int currentCargoInTrain = getCurrentCargoInTrain(train, type);
                                return currentCargoInTrain >= maxCapacityForType;
                            });
                }
                return false; // Can't meet requirements, don't collect cargo

            default:
                return false;
        }
    }

    /**
     * Collects cargo of specified types from a station for a train waiting at the specified stop along its route.
     * The method determines the amount of cargo to collect based on the station's cargo mode,
     * available cargo, and the train's carrying capacity.
     *
     * @param route          The route of the train, containing relevant information about the train and its stops.
     * @param station        The station from which the cargo is to be collected.
     * @param requestedTypes The list of cargo types that need to be collected from the station.
     */
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
            if (availableCargo <= 0) continue;

            int maxCargoTypeCapacity = calculateCargoTypeCapacity(train, cargoType);
            int currentCargoInTrain = getCurrentCargoInTrain(train, cargoType);

            int targetCapacity;
            switch (cargoMode) {
                case HALF:
                    targetCapacity = (int) Math.ceil(maxCargoTypeCapacity * 0.5);
                    break;
                case FULL:
                    targetCapacity = maxCargoTypeCapacity;
                    break;
                case AVAILABLE:
                default:
                    targetCapacity = currentCargoInTrain + Math.min(availableCargo, maxCargoTypeCapacity - currentCargoInTrain);
                    break;
            }

            int remainingCapacity = targetCapacity - currentCargoInTrain;
            if (remainingCapacity > 0) {
                int toCollect = Math.min(availableCargo, remainingCapacity);

                if (toCollect > 0) {
                    loadCargoIntoCarriages(train.getCarriages(), cargoType, toCollect);
                    updateStationCargo(station, cargoType, toCollect);

                    System.out.println("\n[Cargo Collection While Waiting] " + toCollect + " " + cargoType +
                            " cargo collected at " + station.getName() +
                            " (Mode: " + cargoMode + ", Target: " + targetCapacity +
                            ", Current: " + (currentCargoInTrain + toCollect) + "/" + maxCargoTypeCapacity + ")");
                }
            }
        }

        updateStationCapacity(station);
    }

    /**
     * Calculates and retrieves the total capacity of cargo of a specific type currently in the train.
     *
     * @param train the train object containing the list of carriages
     * @param cargoType the specific type of cargo to calculate the total capacity for
     * @return the sum of capacities of the specified cargo type in the train
     */
    private int getCurrentCargoInTrain(Train train, Carriage.Type cargoType) {
        return train.getCarriages().stream()
                .filter(carriage -> carriage.getType() == cargoType)
                .mapToInt(Carriage::getCapacityOwned)
                .sum();
    }

    /**
     * Loads a specified amount of cargo into the provided carriages based on their type and available capacity.
     *
     * @param carriages   the list of carriages to load the cargo into
     * @param cargoType   the type of cargo to be loaded
     * @param amountToLoad the total amount of cargo to load into the carriages
     */
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

    /**
     * Executes the arriving route for a given train, managing various operations such as fuel cost calculation,
     * cargo collection, earnings calculation, and delivery of resources to specific destinations.
     *
     * @param route        The {@code Route} object representing the train's route, including points and train details.
     * @param scenario     The {@code Scenario} object containing the current scenario details such as profit multipliers.
     * @param budget       The {@code Budget} object used to manage the financial status of the train operations.
     * @param selectedMap  A map object representing the selected configuration of the world the train operates in.
     * @param journey      The {@code TrainJourney} instance related to the current operation of the train.
     * @param currentYear  An integer representing the current year of operations, used for reporting and record purposes.
     */
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

    /**
     * Adds the cargo capacity from the train's carriages to the provided CargoCollectionResult.
     * The method iterates through each carriage in the train, identifying its type
     * and updating the corresponding cargo count in the result.
     *
     * @param train the Train object containing carriages whose cargo capacities are to be summed
     * @param result the CargoCollectionResult object where the aggregated cargo capacities will be added
     */
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

    /**
     * Collects and processes cargo from a single station for a given route and train.
     *
     * @param route the route that the train is operating on; must not be null
     * @param station the station from which cargo is to be collected
     * @param availableCargo the amount of cargo available at the station
     * @param cargoType the type of cargo to be collected
     * @return the amount of cargo successfully collected from the station, or 0 if no cargo was collected
     */
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

    /**
     * Finds and returns the PointOfRoute in the given route that corresponds to the specified target station.
     *
     * @param route the route to search within
     * @param targetStation the station to find in the route
     * @return the PointOfRoute corresponding to the target station, or null if the station is not found in the route
     */
    private PointOfRoute findStationInRoute(Route route, Station targetStation) {
        return route.getPoints().stream()
                .filter(point -> point.getStation().equals(targetStation))
                .findFirst()
                .orElse(null);
    }

    /**
     * Calculates the total capacity of carriages of a specific cargo type in a given train.
     *
     * @param train the train containing the carriages to evaluate
     * @param cargoType the type of cargo for which the capacity is to be calculated
     * @return the total capacity of all carriages of the specified cargo type
     */
    private int calculateCargoTypeCapacity(Train train, Carriage.Type cargoType) {
        return train.getCarriages().stream()
                .filter(carriage -> carriage.getType() == cargoType)
                .mapToInt(Carriage::getCapacity)
                .sum();
    }

    /**
     * Retrieves the amount of available cargo of the specified type at the given station.
     *
     * @param station the station from which cargo availability is to be retrieved
     * @param cargoType the type of cargo to check for availability
     * @return the amount of available cargo of the specified type at the station, or 0 if the cargo type is unrecognized
     */
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

    /**
     * Updates the cargo stored in a station by reducing the specified amount of the given cargo type.
     *
     * @param station the station where the cargo is being updated
     * @param cargoType the type of cargo to be reduced (e.g., PEOPLE, MAIL, COAL, IRON, STEEL)
     * @param collectedAmount the amount of cargo to be reduced from the station's storage.
     *                        If the storage does not have enough cargo, the value will be set to zero.
     */
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

    /**
     * Updates the station's total capacity based on the sum of stored cargo across different types.
     *
     * @param station the station object whose capacity is to be updated
     */
    private void updateStationCapacity(Station station) {
        // Calculate total stored cargo across all types
        int totalStored = station.getPeopleStored() + station.getMailStored() +
                station.getCoalStored() + station.getIronStored() + station.getSteelStored();
        station.setCapacityOwned(totalStored);
    }

    /**
     * Updates the CargoCollectionResult based on the specified cargo type and amount.
     *
     * @param result the CargoCollectionResult object to be updated
     * @param cargoType the type of cargo to be added to the result
     * @param amount the amount of the specified cargo type to be added
     */
    private void updateCollectionResult(CargoCollectionResult result, Carriage.Type cargoType, int amount) {
        switch (cargoType) {
            case PEOPLE: result.people += amount; break;
            case MAIL: result.mail += amount; break;
            case COAL: result.coal += amount; break;
            case IRON: result.iron += amount; break;
            case STEEL: result.steel += amount; break;
        }
    }

    /**
     * Checks if a given historical restriction exists in the specified scenario.
     *
     * @param scenario the Scenario object containing historical restrictions
     * @param restriction the name of the restriction to check for
     * @return true if the specified restriction exists in the historical restrictions of the scenario, false otherwise
     */
    private boolean hasHistoricalRestriction(Scenario scenario, String restriction) {
        return scenario.getHistoricalRestrictions().stream()
                .anyMatch(r -> r.equalsIgnoreCase(restriction));
    }

    /**
     * Determines if the train, represented by a list of carriages, is currently full.
     * A train is considered full if all carriages have no available capacity.
     *
     * @param carriages the list of carriages to check; may be null or empty
     * @return true if the train is full or the list of carriages is null or empty, false otherwise
     */
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

    /**
     * CargoCollectionResult is a utility class that holds counts of various categories
     * of cargo, such as people, mail, coal, iron, and steel. It provides methods to compute
     * the total cargo and to determine whether both iron and coal are present.
     */
    private static class CargoCollectionResult {
        int people = 0;
        int mail = 0;
        int coal = 0;
        int iron = 0;
        int steel = 0;

        /**
         * Computes the total cargo by summing the counts of people, mail, and steel.
         *
         * @return The total cargo count as an integer.
         */
        int getTotalCargo() {
            return people + mail + steel;
        }

        /**
         * Determines if both iron and coal are present in the collection.
         *
         * @return true if the collection contains at least one unit of both iron and coal, false otherwise
         */
        boolean hasIronAndCoal() {
            return iron > 0 && coal > 0;
        }
    }

    /**
     * Retrieves a list of industries associated with the given map.
     *
     * @param map the map for which industries need to be retrieved
     * @return a list of industries that belong to the specified map
     */
    public List<Industry> getIndustriesForMap(Map map) {
        return industryRepo.getIndustries().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of cities that match the given map's name.
     *
     * @param map the map object used to filter cities based on its name
     * @return a list of cities associated with the specified map
     */
    public List<City> getCitiesForMap(Map map) {
        return cityRepo.getCities().stream()
                .filter(c -> c.getMapName().equals(map.getMapName()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of stations associated with the current user based on the specified map and scenario.
     *
     * @param map the map containing the context for which stations need to be retrieved
     * @param scenario the scenario context for filtering stations
     * @return a list of stations that match the criteria of belonging to the specified map, scenario,
     *         and being owned by the current user. Returns an empty list if the current user is null.
     */
    public List<Station> getUserStations(Map map, Scenario scenario) {
        if (currentUser == null) return new ArrayList<>();

        return stationrepo.getStations().stream()
                .filter(st -> st.getMapName().equals(map.getMapName()) &&
                        st.getOwnerPlayer().equals(currentUser.getEmail()) &&
                        st.getScenarioName().equals(scenario.getScenarioName()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a runnable task that handles the progression of years in the simulation
     * and performs appropriate actions based on the current year and scenario state.
     *
     * @param selectedScenario the selected scenario containing relevant simulation data
     * @param timePeriod an atomic reference to an {@code AtomicIntegerArray} holding the current year and the end year
     * @param running an atomic boolean indicating whether the simulation is running
     * @param options a list of menu items representing user interaction options
     * @param scheduler a {@code ScheduledExecutorService} for scheduling tasks during the simulation
     * @param gameOver an atomic boolean indicating whether the simulation has ended
     * @param budget the budget object related to the simulation
     * @param sim the simulation instance that handles the core logic of the simulation
     * @return a {@code Runnable} that can be executed to manage year progression within the simulation
     */
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

    /**
     * Handles the progression of the game logic for a specific year, including calculating and managing
     * maintenance costs, updating the budget, processing locomotive changes, and providing a summary of
     * the year's events. If the budget is insufficient, the game will transition to an end state.
     *
     * @param year the current year in the simulation
     * @param budget the budget instance representing the financial state of the user
     * @param selectedScenario the scenario selected by the player
     * @param map the map of the simulation, representing the current geographical scenario
     * @param timePeriod an atomic reference to the array containing key time periods
     * @param scheduler the scheduled executor service responsible for managing periodic tasks
     * @param running an atomic boolean indicating whether the simulation is ongoing
     * @param options a list of menu options available to the user
     * @param gameOver an atomic boolean that is set to true if the game ends
     * @param sim the simulation instance managing the core game logic
     */
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

    /**
     * Calculates and processes the maintenance cost for tracks owned
     * by the current user in the specified scenario and map. The
     * calculated amount is deducted from the provided budget.
     *
     * @param selectedScenario the scenario in which the track ownership is being examined
     * @param map the map where track ownership is derived
     * @param budget the budget from which the maintenance cost is subtracted
     */
    private void payTrackMaintenance(Scenario selectedScenario, Map map,Budget budget) {
        long ownedTracksAmount = railwayRepo.countByScenarioMapAndOwner(selectedScenario, map, currentUser.getEmail());
        int moneySpent = Math.toIntExact(AMOUNT_PER_LINE * ownedTracksAmount);
        yearlyTrackMaintenanceCost = moneySpent;
        budget.subtractBudget(moneySpent);
    }

    /**
     * Displays the yearly financial statistics of the railway system.
     * This includes summarized information about the total earnings,
     * locomotive maintenance costs, track maintenance costs,
     * and fuel costs for the year.
     * The output is printed to the standard console.
     */
    private void displayYearlyStatistics() {
        System.out.println("\n--- Financial Summary ---");
        System.out.printf("Total Earnings from Routes: $%d%n", yearlyEarnings);
        System.out.printf("Locomotive Maintenance Costs: $%d%n", yearlyMaintenanceCost);
        System.out.printf("Track Maintenance Costs: $%d%n", yearlyTrackMaintenanceCost);
        System.out.printf("Fuel Costs: $%d%n", yearlyFuelCost);
        System.out.println("\n------------------------");
    }

    /**
     * Resets all the yearly counters for the system to their default values.
     *
     * This method sets the following yearly metrics to zero:
     * - yearlyEarnings: Tracks the total earnings for the year.
     * - yearlyMaintenanceCost: Tracks the total maintenance costs for the year.
     * - yearlyFuelCost: Tracks the total fuel expenditure for the year.
     * - yearlyTrackMaintenanceCost: Tracks the total cost of maintaining tracks for the year.
     */
    private void resetYearlyCounters() {
        yearlyEarnings = 0;
        yearlyMaintenanceCost = 0;
        yearlyFuelCost = 0;
        yearlyTrackMaintenanceCost = 0;

    }

    /**
     * Announces the locomotives that have become available in the specified year.
     *
     * @param year the year to check for new locomotives starting operation
     */
    private void announceNewLocomotives(int year) {
        Repositories.getInstance().getLocomotiveRepository().getLocomotives().stream()
                .filter(l -> l.getStartYearOfOperation() == year)
                .forEach(l -> System.out.println("Locomotive " + l.getModelName() + " has become available."));
    }

    /**
     * Handles the end of the game by terminating the scheduler, resetting the simulation year,
     * updating the running and gameOver flags, and prompting the user to return to the menu.
     *
     * @param scenario          The current scenario containing the game configuration and time restrictions.
     * @param timePeriod        A reference to the AtomicIntegerArray representing the time period boundaries.
     * @param scheduler         The ScheduledExecutorService used to execute periodic tasks during the game.
     * @param running           An AtomicBoolean flag indicating whether the game loop is running.
     * @param options           The menu options available for user interaction.
     * @param gameOver          An AtomicBoolean flag indicating whether the game has ended.
     * @param sim        The simulation instance associated with the game, used to reset the current year.
     */
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

    /**
     * Calculates the total maintenance cost for all locomotives owned by the current user.
     * It iterates through the user's list of locomotives and sums up their individual maintenance costs.
     *
     * @return the total maintenance cost for all locomotives as an integer
     */
    private int calculateLocomotiveMaintenanceCost() {
        List<Locomotive> allLocomotives = currentUser.getLocomotives();
        int totalMaintenanceCost = 0;

        for (Locomotive locomotive : allLocomotives) {
            totalMaintenanceCost += locomotive.getMaintenanceCost();
        }

        return totalMaintenanceCost;
    }

    /**
     * Generates cargo based on the selected map and scenario, and manages the scheduling and
     * execution of the cargo generation process. This method is synchronized with the provided
     * time period and will shut down the scheduler if the time period conditions are no longer met.
     *
     * @param selectedMap the map used for cargo generation
     * @param selectedScenario the scenario used for determining the cargo generation parameters
     * @param timePeriod a reference to an atomic integer array representing the start and end time of the generation
     * @param running a flag indicating if cargo generation is currently active
     * @param generationScheduler the scheduler responsible for managing asynchronous cargo generation tasks
     */
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

    /**
     * Adds cargo to the stations associated with the specified map and scenario.
     *
     * @param selectedMap       the map object containing details of the selected map
     * @param selectedScenario  the scenario object representing the current scenario
     */
    public void addCargoToStation(Map selectedMap, Scenario selectedScenario) {
        String mapName = selectedMap.getMapName();
        List<Station> stations = getUserStationsForMap(mapName, selectedScenario);
        Set<Station> maxCapacityStations = new HashSet<>();

        addCityCargo(stations, selectedScenario, maxCapacityStations);
        addIndustryCargo(stations, selectedScenario, maxCapacityStations, mapName);
    }

    /**
     * Retrieves a list of stations associated with the specified map and scenario
     * that are owned by the current user.
     *
     * @param mapName the name of the map to filter stations
     * @param scenario the scenario object used to filter stations by scenario name
     * @return a list of stations matching the specified map and scenario that are owned by the current user
     */
    private List<Station> getUserStationsForMap(String mapName, Scenario scenario) {
        return stationrepo.getStations().stream()
                .filter(s -> s.getMapName().equals(mapName) &&
                        s.getScenarioName().equals(scenario.getScenarioName()) &&
                        s.getOwnerPlayer().equals(currentUser.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Distributes city cargo to the appropriate stations based on the provided scenario and capacity limits.
     *
     * @param stations the list of all stations to be considered for distributing city cargo
     * @param selectedScenario the scenario that determines the production rate of city cargo
     * @param maxCapacityStations a set of stations that have reached their maximum cargo capacity
     */
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

    /**
     * Finds and returns a list of stations associated with the specified city.
     *
     * @param stations the list of all stations to filter from
     * @param city the city for which stations are being searched
     * @return a list of stations that are associated with the given city
     */
    private List<Station> findStationsForCity(List<Station> stations, City city) {
        return stations.stream()
                .filter(s -> s.getName().contains(city.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Adds city cargo to the given station, adjusting the station's current stored capacity
     * and ensuring the cargo is divided appropriately between people and mail until the
     * station reaches its maximum capacity.
     *
     * @param station the station where the cargo is to be added
     * @param totalCargo the total amount of cargo to be added to the station
     * @param maxCapacityStations the set of stations that have already reached their maximum capacity
     */
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

    /**
     * Adds industry cargo to stations based on the selected scenario, map name, and station overlap with industries.
     *
     * @param stations The list of stations available for cargo generation.
     * @param selectedScenario The scenario that influences production rates and behavior for cargo distribution.
     * @param maxCapacityStations The set of stations that are operating at maximum capacity, used to determine cargo limits.
     * @param mapName The name of the map used to filter industries for cargo assignment.
     */
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

    /**
     * Determines if a given station's bounds overlap with a given industry's area.
     *
     * @param station the station to check for overlap
     * @param industry the industry whose area to check against the station
     * @return true if the station and industry areas overlap, false otherwise
     */
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

    /**
     * Calculates the bounding box for a given station based on its type and coordinates.
     *
     * @param station the station object for which the bounding box is to be calculated.
     *                It must contain valid coordinates (x, y) and a station type.
     * @return a StationBounds object representing the calculated bounding box.
     *         The bounds are determined based on the station's type and its position.
     */
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

    /**
     * Represents the bounds of a station using a rectangular area defined by
     * minimum and maximum x and y coordinates.
     *
     * This class is immutable and provides a way to encapsulate the boundary
     * dimensions of a station. It includes four integer values: minimum x coordinate,
     * minimum y coordinate, maximum x coordinate, and maximum y coordinate.
     */
    private static class StationBounds {
        final int minX, minY, maxX, maxY;

        StationBounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    /**
     * Distributes the cargo from the given industry to the specified station based on industry type.
     *
     * @param station the station where the cargo will be distributed
     * @param industry the industry producing the cargo
     * @param actualAddedCargo the amount of cargo to be added to the station
     */
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

    /**
     * Determines the amount of cargo generated or available for a specific industry
     * based on the type of industry and its current resources.
     *
     * @param ind the industry for which the cargo amount is calculated
     * @return the amount of cargo generated or available for the given industry,
     *         or 0 if the industry type is not supported or has insufficient resources
     */
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

    /**
     * Adds cargo to the specified station up to its maximum capacity. If the station
     * reaches its maximum capacity as a result of this operation, it is added to the set
     * of stations that have reached maximum capacity.
     *
     * @param station the station to which the cargo is to be added
     * @param cargoToAdd the amount of cargo to add to the station
     * @param maxCapacityStations the set of stations that have reached maximum capacity
     * @return the actual amount of cargo that was added to the station
     */
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

    /**
     * Handles the maintenance and fuel payment for all locomotives associated with the current user.
     * Each locomotive's maintenance is paid using the provided budget.
     *
     * @param budget The budget used to pay for the maintenance and fuel costs of the locomotives.
     */
    public void payLocomotiveMaintenance(Budget budget) {
        List<Locomotive> allLocomotives = currentUser.getLocomotives();
        List<Locomotive> maintainedLocomotives = new ArrayList<>();

        for (Locomotive loc : allLocomotives) {
            payMaintenanceAndFuel(loc, budget);
            maintainedLocomotives.add(loc);
        }
    }

    /**
     * Handles the payment for the maintenance and fuel costs of a locomotive.
     * Updates the locomotive's availability based on the remaining budget.
     *
     * @param locomotive the locomotive for which maintenance and fuel costs need to be paid
     * @param budget the budget from which the costs will be deducted
     */
    private void payMaintenanceAndFuel(Locomotive locomotive, Budget budget) {
        budget.subtractBudget(locomotive.getMaintenanceCost());
        locomotive.setAvailability(locomotive.getFuelCost() <= budget.getBudget());

        if (locomotive.getAvailability()) {
            budget.subtractBudget(yearlyFuelCost / KMS_FUEL);
        }
    }

    /**
     * Removes locomotives from the user's collection that are outdated based on the specified year.
     * A locomotive is considered outdated if its end year of operation is less than
     * or equal to the provided year.
     *
     * @param year the year used as the threshold to remove outdated locomotives
     */
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

    /**
     * Retrieves a list of all simulations from the simulation repository.
     *
     * @return a list of Simulation objects available in the repository
     */
    public List<Simulation> getSimulations() {
        return simulationRepo.getSimulations();
    }

    /**
     * Creates a new simulation by initializing it with the provided map, scenario,
     * current year, and user budget, and then adds it to the simulation repository.
     *
     * @param selectedMap the map selected for the simulation
     * @param selectedScenario the scenario chosen for the simulation
     * @param currentYear the current year in the context of the simulation
     * @param userBudget the budget assigned to the user for the simulation
     * @return the newly created Simulation instance
     */
    public Simulation createSimulation(Map selectedMap, Scenario selectedScenario, int currentYear, Budget userBudget) {
        return simulationRepo.add(new Simulation(selectedMap, selectedScenario, currentUser, currentYear, userBudget));
    }

    /**
     * Retrieves and allows the user to select a scenario from the available list of scenarios.
     * If no scenarios are available or the user does not select an option, null is returned.
     *
     * @return the selected Scenario object if the user makes a valid selection,
     *         or null if no scenarios are available or no valid selection is made.
     */
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

    /**
     * Checks for an existing budget associated with the given map, scenario, and current user.
     * If no such budget exists, creates a new budget with default values and saves it.
     *
     * @param selectedMap the map associated with the budget lookup or creation
     * @param selectedScenario the scenario associated with the budget lookup or creation
     * @return the existing budget if found, otherwise a newly created budget
     */
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

    /**
     * Updates the current simulation with the provided parameters.
     *
     * @param map the map configuration to be used for the simulation
     * @param scenario the scenario settings to be applied to the simulation
     * @param newYear the year to update the simulation to
     * @param newBudget the new budget to be allocated for the simulation
     */
    public void updateSimulation(Map map, Scenario scenario, int newYear, Budget newBudget) {
        simulationRepo.updateSimulation(map, scenario, currentUser, newYear, newBudget);
    }

    /**
     * Resets the game by initializing locomotives, setting the budget, and removing
     * stations associated with the specified map, scenario, and user.
     *
     * @param selectedMap the map selected for the game
     * @param selectedScenario the scenario selected for the game
     * @param budget the budget object to be updated with a new value
     */
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

    /**
     * Finds a simulation by the provided map and scenario.
     *
     * @param map a map containing relevant data for the simulation
     * @param scenario the scenario to be used for finding the simulation
     * @return an Optional containing the found Simulation, or an empty Optional if no simulation is found
     */
    public Optional<Simulation> findBy(Map map, Scenario scenario) {
        return simulationRepo.findBy(map, scenario, currentUser);
    }
}