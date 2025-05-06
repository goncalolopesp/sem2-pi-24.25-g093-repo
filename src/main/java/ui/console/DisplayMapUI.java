package ui.console;

import controller.DisplayMapController;
import domain.City;
import domain.HouseBlock;
import domain.Map;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * User interface to displays the a map with its cities and house blocks.
 * <p>
 * Presents a list of available maps, asks the user to select one by name,
 * and then renders the map grid to the console, marking city centers and blocks.
 * </p>
 *
 * @see DisplayMapController
 */


public class DisplayMapUI implements Runnable{
    /** Controller responsible for retrieving map and city data. */
    private final DisplayMapController ctrl = new DisplayMapController();
    private String mapName;
    private String[][] mapArray;

    /**
     * Runs the user interface section.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Map List ------------------------");
        displayMapList();
        requestData();
        submitData();
        System.out.println();
        displayMap();
    }

    /**
     * Prints the map grid on the console.
     */
    private void displayMap() {
        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray[0].length; j++) {
                if (j == mapArray[0].length - 1) {
                    System.out.print(mapArray[i][j]); // no space after the last one
                } else {
                    System.out.print(mapArray[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Displays the list of available maps retrieved from the controller.
     */
    private void displayMapList() {
        System.out.println(ctrl.getMaps());
    }

    /**
     * Initializes the map array based on selected map dimensions, populates it,
     * and returns the array for rendering.
     *
     * @return the array that represents the map
     */
    private String[][] submitData() {
        int width = 0; int height = 0;
        Optional<Map> matchingMap = ctrl.getMaps().stream().filter(map -> map.getMapName().equals(mapName)).findFirst();
        if (ctrl.verifyIfMapExists(mapName)) {
            width = matchingMap.get().getWidth();
            height = matchingMap.get().getHeight();
        } else {
            System.out.println("\nMap doesn't exist!");
        }

        mapArray = new String[height][width];

        for (int i = 0; i < mapArray.length; i++) {
            for (int j = 0; j < mapArray[0].length; j++) {
                mapArray[i][j] = "⬛";
            }
        }

        addCitiesToMap();
        addHouseBlocksToMap();

        return mapArray;
    }

    /**
     * Marks the city centers on the map array by placing the city symbol at each center.
     */
    private void addCitiesToMap() {
        List<City> citiesList = ctrl.getCities();
        for (int i = 0; i < citiesList.size(); i++) {
            if (citiesList.stream().anyMatch(city -> city.getMapName().equals(mapName))){
                int centerX = citiesList.get(i).getCenterX();
                int centerY = citiesList.get(i).getCenterY();
                mapArray[centerY-1][centerX-1] = "🌆";
            }
        }
    }

    /**
     * Marks house blocks on the map array by placing the block symbol at each block position.
     */
    private void addHouseBlocksToMap() {
        List<City> citiesList = ctrl.getCities();
        for (int i = 0; i < citiesList.size(); i++) {
            if (citiesList.stream().anyMatch(city -> city.getMapName().equals(mapName))){
                List<HouseBlock> houseBlockList = citiesList.get(i).getBlocks();
                for (int j = 0; j < houseBlockList.size(); j++) {
                    int posX = houseBlockList.get(j).getX();
                    int posY = houseBlockList.get(j).getY();
                    mapArray[posY-1][posX-1] = "🌆";
                }
            }
        }
    }

    /**
     * Asks the user to enter the name of the map to display.
     */
    private void requestData() {
        mapName = requestMapName();
    }

    /**
     * Reads the map name from the console input and trims whitespace.
     *
     * @return the entered and trimmed map name
     */
    private String requestMapName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Select Map: ");
        return sc.nextLine().trim();
    }
}
