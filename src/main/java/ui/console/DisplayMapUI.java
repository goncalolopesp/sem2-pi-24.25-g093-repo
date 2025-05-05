package ui.console;

import controller.DisplayMapController;
import domain.Map;

import java.util.Optional;
import java.util.Scanner;

public class DisplayMapUI implements Runnable{
    private final DisplayMapController ctrl = new DisplayMapController();
    private String mapName;
    private int[][] mapArray;

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
                    System.out.print("0"); // no space after the last one
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
    }

    private void displayMapList() {
        System.out.println(ctrl.getMaps());
    }

    private int[][] submitData() {
        int width = 0; int height = 0;
        Optional<Map> matchingMap = ctrl.getMaps().stream().filter(map -> map.getMapName().equals(mapName)).findFirst();
        if (ctrl.verifyIfMapExists(mapName)) {
            width = matchingMap.get().getWidth();
            height = matchingMap.get().getHeight();
        } else {
            System.out.println("\nMap doesn't exist!");
        }
        return mapArray = new int[width][height];
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
