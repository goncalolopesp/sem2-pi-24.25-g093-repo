package ui.console;

import controller.CreateCityController;
import controller.CreateCityController.CenterError;
import controller.CreateCityController.NameMapError;
import controller.CreateCityController.BlockError;

import domain.City;
import domain.HouseBlock;

import java.util.Scanner;

/**
 * Launches the user interface responsible for the creation of the city on a certain map.
 */

public class CreateCityUI implements Runnable {
    /** Controller handling city creation and validation. */
    private final CreateCityController ctrl = new CreateCityController();

    /**
     * Runs the "Create City" user interface.
     * <p>
     * Asks for city and map names,as well as coordinates for the city center, validates inputs, creates the city,
     * then allows manual or automatic block assignment.
     * </p>
     */
    @Override public void run() {
        System.out.println("\n\n--- Create City ------------------------");
        Scanner sc = new Scanner(System.in);
        String cityName, mapName;

        NameMapError nm;
        do {
            System.out.print("Enter City Name: "); cityName = sc.nextLine().trim();
            System.out.print("Enter Map Name: "); mapName = sc.nextLine().trim();
            nm = ctrl.validateNameMap(cityName, mapName);
            if (nm == NameMapError.MAP_NOT_FOUND)
                System.out.println("Error: Map '" + mapName + "' does not exist.");
            else if (nm == NameMapError.NAME_DUPLICATE)
                System.out.println("Error: City name '" + cityName + "' already used on map '" + mapName + "'.");
        } while (nm != NameMapError.OK);

        int cx, cy;
        CenterError ce;
        do {
            System.out.print("Enter Center X: "); cx = Integer.parseInt(sc.nextLine());
            System.out.print("Enter Center Y: "); cy = Integer.parseInt(sc.nextLine());
            ce = ctrl.validateCenter(cityName, mapName, cx, cy);
            if (ce == CenterError.OUT_OF_BOUNDS)
                System.out.println("Error: Center (" + cx + "," + cy + ") outside map bounds.");
            else if (ce == CenterError.POSITION_TAKEN)
                System.out.println("Error: Another city occupies (" + cx + "," + cy + ").");
            else if (ce == CenterError.BLOCK_PRESENT)
                System.out.println("Error: A house block exists at (" + cx + "," + cy + ").");
        } while (ce != CenterError.OK);

        City city = ctrl.createCity(cityName, mapName, cx, cy);
        System.out.println("City created: " + city.getName() + " on map " + city.getMapName());

        char mode;
        do {
            System.out.print("Choose block mode (M=Manual, A=Auto): ");
            String line = sc.nextLine().trim().toUpperCase();
            mode = line.isEmpty() ? ' ' : line.charAt(0);
        } while (mode != 'M' && mode != 'A');

        if (mode == 'M') {
            System.out.print("How many blocks to add? ");
            int n = Integer.parseInt(sc.nextLine());
            for (int i = 0; i < n; i++) {
                HouseBlock b;
                BlockError be;
                do {
                    System.out.print("Block " + (i+1) + " X: "); int bx = Integer.parseInt(sc.nextLine());
                    System.out.print("Block " + (i+1) + " Y: "); int by = Integer.parseInt(sc.nextLine());
                    b = new HouseBlock(bx, by);
                    be = ctrl.validateBlock(city.getMapName(), b);
                    if (be == BlockError.OUT_OF_BOUNDS)
                        System.out.println("Error: Block (" + bx + "," + by + ") outside map bounds.");
                    else if (be == BlockError.COLLIDES_CITY_CENTER)
                        System.out.println("Error: Block collides with a city center at (" + bx + "," + by + ").");
                    else if (be == BlockError.COLLIDES_BLOCK)
                        System.out.println("Error: Block conflicts with existing block at (" + bx + "," + by + ").");
                } while (be != BlockError.OK);
                ctrl.addBlockManual(city, b);
            }
            System.out.println("Blocks assigned manually.");
        } else {
            System.out.print("Number of blocks: "); int n = Integer.parseInt(sc.nextLine());
            System.out.print("Sigma (spread): "); double s = Double.parseDouble(sc.nextLine());
            ctrl.assignBlocksAuto(city, n, s);
            System.out.println("Blocks assigned automatically.");
        }

        System.out.println("City created with success.");
    }
}
