package domain;

import java.util.Map;

/**
 * Represents a Carriage associated to a Train
 */
public class Carriage {
    /**
     * Represents the different types of carriage
     */
    public enum Type {
        PEOPLE(70),
        MAIL(30),
        COAL(50),
        IRON(40),
        STEEL(35);

        private final int defaultCapacity;

        Type(int defaultCapacity) {
            this.defaultCapacity = defaultCapacity;
        }

        public int getDefaultCapacity() {
            return defaultCapacity;
        }
    }

    private final Type type;
    private final int capacity;
    private int capacityOwned = 0;

    /**
     * Creates a carriage with default capacity for the given type
     * @param type the type of the carriage
     */
    public Carriage(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.type = type;
        this.capacity = type.getDefaultCapacity();
    }

    /**
     * Creates a carriage with custom capacity
     * @param type the type of the carriage
     * @param capacity the custom capacity
     */
    public Carriage(Type type, int capacity) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.type = type;
        this.capacity = capacity;
    }

    /**
     * Returns the max capacity of this carriage
     * @return the max capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the type of this carriage
     * @return the carriage type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the current occupied capacity
     * @return the current occupied capacity
     */
    public int getCapacityOwned() {
        return capacityOwned;
    }

    /**
     * Sets the current occupied capacity
     * @param capacityOwned the capacity to set
     */
    public void setCapacityOwned(int capacityOwned) {
        if (capacityOwned < 0 || capacityOwned > capacity) {
            throw new IllegalArgumentException("Capacity owned must be between 0 and " + capacity);
        }
        this.capacityOwned = capacityOwned;
    }

    /**
     * Returns the remaining available capacity
     * @return available capacity
     */
    public int getAvailableCapacity() {
        return capacity - capacityOwned;
    }

    /**
     * Checks if the carriage is full
     * @return true if full, false otherwise
     */
    public boolean isFull() {
        return capacityOwned >= capacity;
    }

    /**
     * Adds cargo to the carriage if there's space
     * @param amount the amount to add
     * @return true if successfully added, false if not enough space
     */
    public boolean addCargo(int amount) {
        if (amount <= 0 || capacityOwned + amount > capacity) {
            return false;
        }
        capacityOwned += amount;
        return true;
    }

    /**
     * Removes cargo from the carriage
     * @param amount the amount to remove
     * @return true if successfully removed, false if not enough cargo
     */
    public boolean removeCargo(int amount) {
        if (amount <= 0 || capacityOwned < amount) {
            return false;
        }
        capacityOwned -= amount;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Carriage[type=%s, capacity=%d/%d]",
                type, capacityOwned, capacity);
    }
}