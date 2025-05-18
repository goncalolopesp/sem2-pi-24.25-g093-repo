package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocomotiveTest {
    private Locomotive locomotive;

    private final String modelName = "Big Boy";
    private final int price = 5000;
    private final String type = "Steam";
    private final int fuelCost = 200;
    private final int maintenanceCost = 150;
    private final int startYearOfOperation = 1940;
    private final int endYearOfOperation = 1960;
    private final int topSpeed = 130;

    @BeforeEach
    void setUp() {
        locomotive = new Locomotive(
                modelName,
                price,
                type,
                fuelCost,
                maintenanceCost,
                startYearOfOperation,
                endYearOfOperation,
                topSpeed
        );
    }

    @Test
    void getModelName() {
        assertEquals(modelName, locomotive.getModelName());
    }

    @Test
    void setModelName() {
        String newModelName = "GG1";
        locomotive.setModelName(newModelName);
        assertEquals(newModelName, locomotive.getModelName());
    }

    @Test
    void getPrice() {
        assertEquals(price, locomotive.getPrice());
    }

    @Test
    void setPrice() {
        int newPrice = 6000;
        locomotive.setPrice(newPrice);
        assertEquals(newPrice, locomotive.getPrice());
    }

    @Test
    void getType() {
        assertEquals(type, locomotive.getType());
    }

    @Test
    void setType() {
        String newType = "Diesel";
        locomotive.setType(newType);
        assertEquals(newType, locomotive.getType());
    }

    @Test
    void getFuelCost() {
        assertEquals(fuelCost, locomotive.getFuelCost());
    }

    @Test
    void setFuelCost() {
        int newFuelCost = 180;
        locomotive.setFuelCost(newFuelCost);
        assertEquals(newFuelCost, locomotive.getFuelCost());
    }

    @Test
    void getMaintenanceCost() {
        assertEquals(maintenanceCost, locomotive.getMaintenanceCost());
    }

    @Test
    void setMaintenanceCost() {
        int newMaintenanceCost = 175;
        locomotive.setMaintenanceCost(newMaintenanceCost);
        assertEquals(newMaintenanceCost, locomotive.getMaintenanceCost());
    }

    @Test
    void getStartYearOfOperation() {
        assertEquals(startYearOfOperation, locomotive.getStartYearOfOperation());
    }

    @Test
    void setStartYearOfOperation() {
        int newStartYear = 1945;
        locomotive.setStartYearOfOperation(newStartYear);
        assertEquals(newStartYear, locomotive.getStartYearOfOperation());
    }

    @Test
    void getEndYearOfOperation() {
        assertEquals(endYearOfOperation, locomotive.getEndYearOfOperation());
    }

    @Test
    void setEndYearOfOperation() {
        int newEndYear = 1965;
        locomotive.setEndYearOfOperation(newEndYear);
        assertEquals(newEndYear, locomotive.getEndYearOfOperation());
    }

    @Test
    void getTopSpeed() {
        assertEquals(topSpeed, locomotive.getTopSpeed());
    }

    @Test
    void setTopSpeed() {
        int newTopSpeed = 140;
        locomotive.setTopSpeed(newTopSpeed);
        assertEquals(newTopSpeed, locomotive.getTopSpeed());
    }

    @Test
    void getAvailability() {
        assertTrue(locomotive.getAvailability());
    }

    @Test
    void setAvailability() {
        locomotive.setAvailability(false);
        assertFalse(locomotive.getAvailability());

        locomotive.setAvailability(true);
        assertTrue(locomotive.getAvailability());
    }

    @Test
    void constructorSetsInitialValues() {
        String testModel = "Challenger";
        int testPrice = 4500;
        String testType = "Electric";
        int testFuelCost = 150;
        int testMaintenanceCost = 100;
        int testStartYear = 1950;
        int testEndYear = 1970;
        int testTopSpeed = 120;

        Locomotive testLocomotive = new Locomotive(
                testModel,
                testPrice,
                testType,
                testFuelCost,
                testMaintenanceCost,
                testStartYear,
                testEndYear,
                testTopSpeed
        );

        assertEquals(testModel, testLocomotive.getModelName());
        assertEquals(testPrice, testLocomotive.getPrice());
        assertEquals(testType, testLocomotive.getType());
        assertEquals(testFuelCost, testLocomotive.getFuelCost());
        assertEquals(testMaintenanceCost, testLocomotive.getMaintenanceCost());
        assertEquals(testStartYear, testLocomotive.getStartYearOfOperation());
        assertEquals(testEndYear, testLocomotive.getEndYearOfOperation());
        assertEquals(testTopSpeed, testLocomotive.getTopSpeed());
        assertTrue(testLocomotive.getAvailability());
    }
}