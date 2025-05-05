package ui.console;

import controller.DisplayMapController;
import domain.City;
import domain.HouseBlock;
import domain.Map;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class DisplayMapUI implements Runnable{
    private final DisplayMapController ctrl = new DisplayMapController();
    private String mapName;
    private String[][] mapArray;

    @Override
    public void run() {
        System.out.println("\n\n--- Map List ------------------------");
        displayMapList();
        requestData();
        submitData();
        System.out.println();
        displayMap();
    }
    
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

    private void displayMapList() {
        System.out.println(ctrl.getMaps());
    }

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

    private void addCitiesToMap() {
        List<City> citiesList = ctrl.getCities();
        for (int i = 0; i < ctrl.getCities().size(); i++) {
            int centerX = citiesList.get(i).getCenterX();
            int centerY = citiesList.get(i).getCenterY();
            mapArray[centerY-1][centerX-1] = "🌆";
        }
    }

    private void addHouseBlocksToMap() {
        List<City> citiesList = ctrl.getCities();
        for (int i = 0; i < ctrl.getCities().size(); i++) {
            List<HouseBlock> houseBlockList = citiesList.get(i).getBlocks();
            for (int j = 0; j < houseBlockList.size(); j++) {
                int posX = houseBlockList.get(j).getX();
                int posY = houseBlockList.get(j).getY();
                mapArray[posY-1][posX-1] = "🌆";
            }
        }
    }

    private void requestData() {
        mapName = requestMapName();
    }

    private String requestMapName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Select Map: ");
        return sc.nextLine().trim();
    }
}
