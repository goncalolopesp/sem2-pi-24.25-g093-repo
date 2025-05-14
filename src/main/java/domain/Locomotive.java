package domain;

public class Locomotive {

    private String modelName;
    private int price;
    private String type;
    private int fuelCost;
    private int maintenanceCost;
    private int startYearOfOperation;
    private int endYearOfOperation;
    private int topSpeed;
    private boolean available;

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

    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getFuelCost() {
        return fuelCost;
    }
    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }
    public int getMaintenanceCost() {
        return maintenanceCost;
    }
    public void setMaintenanceCost(int maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }
    public int getStartYearOfOperation() {
        return startYearOfOperation;
    }
    public void setStartYearOfOperation(int startYearOfOperation) {
        this.startYearOfOperation = startYearOfOperation;
    }
    public int getEndYearOfOperation() {
        return endYearOfOperation;
    }
    public void setEndYearOfOperation(int endYearOfOperation) {
        this.endYearOfOperation = endYearOfOperation;
    }
    public int getTopSpeed() {
        return topSpeed;
    }
    public void setTopSpeed(int topSpeed) {
        this.topSpeed = topSpeed;
    }
    public boolean getAvailability() { return available; }
    public void setAvailability(boolean available) { this.available = available; }

}
