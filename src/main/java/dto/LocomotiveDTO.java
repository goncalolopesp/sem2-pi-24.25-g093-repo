package dto;

import domain.Locomotive;

/**
 * A Data Transfer Object (DTO) for representing a locomotive's basic information.
 * This class encapsulates the essential details of a locomotive
 * and is used to transfer this specific data between different layers of the application.
 */
public class LocomotiveDTO {
    private final String modelName;
    private final int price;
    private final String type;
    private final int topSpeed;
    private final boolean available;

    /**
     * Constructs a new LocomotiveDTO instance.
     *
     * @param modelName the model name of the locomotive
     * @param price the price of the locomotive
     * @param type the type of the locomotive
     * @param topSpeed the top speed of the locomotive
     * @param available the availability status of the locomotive
     */
    public LocomotiveDTO(String modelName, int price, String type, int topSpeed, boolean available) {
        this.modelName = modelName;
        this.price = price;
        this.type = type;
        this.topSpeed = topSpeed;
        this.available = available;
    }

    /**
     * Converts a Locomotive entity into a LocomotiveDTO.
     *
     * @param locomotive the Locomotive entity to convert
     * @return a LocomotiveDTO representing the basic information of the Locomotive entity
     */
    public static LocomotiveDTO fromEntity(Locomotive locomotive) {
        return new LocomotiveDTO(
                locomotive.getModelName(),
                locomotive.getPrice(),
                locomotive.getType(),
                locomotive.getTopSpeed(),
                locomotive.getAvailability()
        );
    }

    /**
     * Retrieves the model name of the locomotive.
     *
     * @return the model name of the locomotive
     */
    public String getModelName() { return modelName; }

    /**
     * Retrieves the price of the locomotive.
     *
     * @return the price of the locomotive
     */
    public int getPrice() { return price; }

    /**
     * Retrieves the type of the locomotive.
     *
     * @return the type of the locomotive
     */
    public String getType() { return type; }

    /**
     * Retrieves the top speed of the locomotive.
     *
     * @return the top speed of the locomotive
     */
    public int getTopSpeed() { return topSpeed; }

    /**
     * Retrieves the availability status of the locomotive.
     *
     * @return true if the locomotive is available, false otherwise
     */
    public boolean isAvailable() { return available; }

    @Override
    public String toString() {
        return String.format("%s (%s) - $%d - Max Speed: %d km/h%s",
                modelName, type, price, topSpeed,
                available ? " [Available]" : " [Unavailable]");
    }
}