// src/main/java/ui/console/CreateCityUI.java
package ui.console;

import controller.CreateCityController;
import domain.HouseBlock;
import domain.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for creating a city and assigning house blocks.
 * Prompts each input in its own loop until valid, without using while(true).
 */
public class CreateCityUI implements Runnable {
    private final CreateCityController ctrl = new CreateCityController();

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String cityName = "";
        String mapName = "";
        int centerX = 0, centerY = 0;
        City city = null;

        // Name & Map
        boolean validNameMap = false;
        while (!validNameMap) {
            System.out.print("Enter City Name: ");
            cityName = sc.nextLine().trim();
            System.out.print("Enter Map Name: ");
            mapName = sc.nextLine().trim();
            if (ctrl.createCity(cityName, mapName, 0, 0) != null) {
                validNameMap = true;
            } else {
                System.out.println("Invalid or duplicate city name/map. Try again.");
            }
        }

        // Center Coordinates
        boolean validCenter = false;
        while (!validCenter) {
            System.out.print("Enter Center X: ");
            centerX = Integer.parseInt(sc.nextLine());
            System.out.print("Enter Center Y: ");
            centerY = Integer.parseInt(sc.nextLine());
            city = ctrl.createCity(cityName, mapName, centerX, centerY);
            if (city != null) {
                validCenter = true;
            } else {
                System.out.println("Center out of bounds or city exists. Try again.");
            }
        }

        System.out.println("City created.");

        // Blocks Assignment Mode
        System.out.print("Choose block mode (M=Manual, A=Auto): ");
        String mode = sc.nextLine().trim().toUpperCase();
        while (!mode.equals("M") && !mode.equals("A")) {
            System.out.print("Invalid mode. Enter 'M' or 'A': ");
            mode = sc.nextLine().trim().toUpperCase();
        }

        if (mode.equals("M")) {
            List<HouseBlock> blocks = new ArrayList<>();
            System.out.print("How many blocks to add? ");
            int n = Integer.parseInt(sc.nextLine());
            for (int i = 0; i < n; i++) {
                System.out.print("Block " + (i + 1) + " X: ");
                int bx = Integer.parseInt(sc.nextLine());
                System.out.print("Block " + (i + 1) + " Y: ");
                int by = Integer.parseInt(sc.nextLine());
                blocks.add(new HouseBlock(bx, by));
            }
            if (ctrl.assignBlocksManual(city, blocks)) {
                System.out.println("Blocks assigned manually.");
            } else {
                System.out.println("Block conflict. No blocks were assigned.");
            }
        } else {
            System.out.print("Number of blocks: ");
            int n = Integer.parseInt(sc.nextLine());
            System.out.print("Sigma (spread): ");
            double s = Double.parseDouble(sc.nextLine());
            ctrl.assignBlocksAuto(city, n, s);
            System.out.println("Blocks assigned automatically.");
        }

        System.out.println("Done.");
    }
}
