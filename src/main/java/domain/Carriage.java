package domain;

/**
 * Represents a Carriage associated to a Train
 */
public class Carriage {
    /**
     * Represents the different types of carriage
     */
    public enum Type{
        PEOPLE,MAIL
    }

    private final Type type;
    private final int capacity;
    private int capacityOwned = 0;

    /**
     * Represents a constructor to create a carriage
     * @param type represents the type of the carriage
     */
    public Carriage(Type type) {
        if (type==null){
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.type = type;
        switch (type) {
            case PEOPLE:
                this.capacity = 70;
                break;
            case MAIL:
                this.capacity = 30;
                break;
            default:
                throw new IllegalStateException("Unhandled carriage type: " + type);
        }
    }

    /**
     * Returns the max capacity of a certain carriage.
     *
     * @return the max capacity of a certain carriage
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the type of a certain carriage.
     *
     * @return the type of a certain carriage.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the current occupied capacity of a certain carriage.
     *
     * @return the current occupied capacity of a certain carriage..
     */
    public int getCapacityOwned() {
        return capacityOwned;
    }

    /**
     * Sets the current occupied capacity of a certain carriage.
     *
     * @param capacityOwned the current occupied capacity of a certain carriage..
     */
    public void setCapacityOwned(int capacityOwned) {
        this.capacityOwned = capacityOwned;
    }

    /**
     * Returns a String formatted output of the carriage
     *
     * @return a String formatted output of the carriage
     */
    public String toString() {
        return "Carriage type: " + type + "capacity: " + capacity;
    }
}
