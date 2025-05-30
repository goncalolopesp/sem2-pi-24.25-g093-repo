package ui.console;

import controller.CreateMapController;
import domain.Map;

import java.util.Optional;
import java.util.Scanner;

/**
 * Launches the user interface responsible for the creation of a map.
 */

public class CreateMapUI implements Runnable{
    /** Controller responsible for map creation logic. */
    private final CreateMapController ctrl;
    private String mapName;
    private int width;
    private int height;
    private int scale;

    /**
     * Constructs a new CreateMapUI, initializing its controller.
     */
    public CreateMapUI() { ctrl = new CreateMapController(); }

    /**
     * Returns the controller used to create maps.
     *
     * @return the CreateMapController instance
     */
    private CreateMapController getMapController() { return ctrl; }

    /**
     * Runs the user interface section.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Create Map ------------------------");
        requestData();
        submitData();
        System.out.println("Do you want to save the map as a file? (y/n)");
        Scanner sc = new Scanner(System.in);
        String answer = sc.nextLine().trim();
        if (answer.equalsIgnoreCase("y")) {
            saveMap();
        }
    }

    public void saveMap() {
        if (getMapController().saveMap(mapName, width, height, scale)) {
            if (getMapController().saveMaps()){
                System.out.println("Map saved successfully!");
            }
        }
    }

    /**
     * Collects all data related to map creation.
     * Prints a success or failure message based on the result.
     */
    private void submitData() {
        Optional<Map> map = Optional.ofNullable(getMapController().createMap(mapName, width, height, scale));

        if (map.isPresent()) {
            System.out.println("\nMap successfully created!");
        } else {
            System.out.println("\nMap not created!");
        }
    }

    /**
     * Asks the user for map name, width, and height.
     */
    private void requestData() {
        mapName = requestMapName();
        width = requestWidth();
        height = requestHeight();
        scale = requestScale();
    }

    /**
     * Asks the user to enter a valid map height.
     * <p>
     * Repeats until a positive integer (1-256) is provided.
     * </p>
     *
     * @return the validated map height
     */
    private int requestHeight() {
        Scanner sc = new Scanner(System.in);
        int h = 0;

        System.out.print("Enter Map Height: ");

        do {
            String input = sc.nextLine().trim();
            try {
                h = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number. ");
            }
            if (h <= 0 || h > 256) {
                System.out.print("Invalid height. Must be positive and below 256. Try again: ");
            }
        } while (h <= 0 || h > 256);

        return h;
    }

    /**
     * Asks the user to enter a valid map width.
     * <p>
     * Repeats until a positive integer (1-256) is provided.
     * </p>
     *
     * @return the validated map width
     */
    private int requestWidth() {
        Scanner sc = new Scanner(System.in);
        int w = 0;

        System.out.print("Enter Map Width: ");

        do {
            String input = sc.nextLine().trim();
            try {
                w = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number. ");
            }
            if (w <= 0 || w > 256) {
                System.out.print("Invalid width. Must be positive and below 256. Try again: ");
            }
        } while (w <= 0 || w > 256);

        return w;
    }

    /**
     * Asks the user to enter a map name and trims whitespace.
     * <p>
     * Repeats process until user provides a unique name
     * </p>
     *
     * @return the entered and trimmed map name
     */
    private String requestMapName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Map Name: ");
        String mapName;
        String pattern = "^[^\\\\/:*?\"<>|\\r\\n]+$";

        do {
            mapName = sc.nextLine();
            if (!ctrl.checkNameUniqueness(mapName)){
                System.out.print("\nName not unique. Try again: ");
            } else if (!mapName.matches(pattern)) {
                System.out.print("\nInvalid name. Must contain only alphanumeric characters and spaces. Try again: ");
            } else if (mapName.isEmpty()) {
                System.out.print("\nName cannot be empty. Try again: ");
            }
        } while(!ctrl.checkNameUniqueness(mapName) || !mapName.matches(pattern) || mapName.isEmpty());

        return mapName.trim();
    }

    private int requestScale() {
        Scanner sc = new Scanner(System.in);
        int s = 0;

        System.out.print("Enter Map Scale (km): ");

        do {
            String input = sc.nextLine().trim();
            try {
                s = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Error: '" + input + "' is not a valid integer. Please enter a number. ");
            }
            if (s <= 0 || s > 256) {
                System.out.print("Invalid scale. Must be positive and below 256. Try again: ");
            }
        } while (s <= 0 || s > 256);

        return s;
    }
}
