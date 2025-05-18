package domain;

/**
 * Locomotive entity representing a train engine in the game
 * Contains all properties and characteristics of train locomotives
 */
public class Locomotive {

    /** Model name identifier for the locomotive */
    private String modelName;

    /** Purchase price of the locomotive */
    private int price;

    /** Type of locomotive (e.g., steam, diesel, electric) */
    private String type;

    /** Cost of fuel per operation */
    private int fuelCost;

    /** Cost of maintenance for the locomotive */
    private int maintenanceCost;

    /** First year this locomotive model is available for purchase */
    private int startYearOfOperation;

    /** Last year this locomotive model is available for purchase */
    private int endYearOfOperation;

    /** Maximum speed of the locomotive */
    private int topSpeed;

    /** Indicates if the locomotive is available for purchase */
    private boolean available;

    /**
     * Creates a new Locomotive with the specified characteristics
     * @param modelName The model name of the locomotive
     * @param price The purchase price of the locomotive
     * @param type The type of locomotive (e.g., steam, diesel, electric)
     * @param fuelCost The fuel cost per operation
     * @param maintenanceCost The maintenance cost
     * @param startYearOfOperation The first year this locomotive is available
     * @param endYearOfOperation The last year this locomotive is available
     * @param topSpeed The maximum speed of the locomotive
     */
    public Locomotive(String modelName, int price, String type, int fuelCost, int maintenanceCost, int startYearOfOperation, int endYearOfOperation, int topSpeed) {
        this.modelName = modelName;
        this.price = price;
        this.type = type;
        this.fuelCost = fuelCost;
        this.maintenanceCost = maintenanceCost;
        this.startYearOfOperation = startYearOfOperation;
        this.endYearOfOperation = endYearOfOperation;
        this.topSpeed = topSpeed;
        this.available = true;
    }

    /**
     * Gets the model name of the locomotive
     * @return The locomotive's model name
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name of the locomotive
     * @param modelName The new model name
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Gets the purchase price of the locomotive
     * @return The locomotive's price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the purchase price of the locomotive
     * @param price The new price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the type of the locomotive
     * @return The locomotive's type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the locomotive
     * @param type The new locomotive type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the fuel cost of the locomotive
     * @return The locomotive's fuel cost
     */
    public int getFuelCost() {
        return fuelCost;
    }

    /**
     * Sets the fuel cost of the locomotive
     * @param fuelCost The new fuel cost
     */
    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }

    /**
     * Gets the maintenance cost of the locomotive
     * @return The locomotive's maintenance cost
     */
    public int getMaintenanceCost() {
        return maintenanceCost;
    }

    /**
     * Sets the maintenance cost of the locomotive
     * @param maintenanceCost The new maintenance cost
     */
    public void setMaintenanceCost(int maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    /**
     * Gets the start year of operation for the locomotive
     * @return The first year this locomotive is available
     */
    public int getStartYearOfOperation() {
        return startYearOfOperation;
    }

    /**
     * Sets the start year of operation for the locomotive
     * @param startYearOfOperation The new start year
     */
    public void setStartYearOfOperation(int startYearOfOperation) {
        this.startYearOfOperation = startYearOfOperation;
    }

    /**
     * Gets the end year of operation for the locomotive
     * @return The last year this locomotive is available
     */
    public int getEndYearOfOperation() {
        return endYearOfOperation;
    }

    /**
     * Sets the end year of operation for the locomotive
     * @param endYearOfOperation The new end year
     */
    public void setEndYearOfOperation(int endYearOfOperation) {
        this.endYearOfOperation = endYearOfOperation;
    }

    /**
     * Gets the top speed of the locomotive
     * @return The locomotive's maximum speed
     */
    public int getTopSpeed() {
        return topSpeed;
    }

    /**
     * Sets the top speed of the locomotive
     * @param topSpeed The new maximum speed
     */
    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }

    /**
     * Gets the availability status of the locomotive
     * @return true if the locomotive is available for purchase, false otherwise
     */
    public boolean getAvailability() { return available; }

    /**
     * Sets the availability status of the locomotive
     * @param available The new availability status
     */
    public void setAvailability(boolean available) { this.available = available; }
}