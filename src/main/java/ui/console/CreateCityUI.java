package ui.console;

import controller.CreateCityController;
import controller.CreateCityController.CenterError;
import controller.CreateCityController.NameMapError;
import controller.CreateCityController.BlockError;

import domain.City;
import domain.HouseBlock;
import repository.MapRepository;
import repository.Repositories;

import java.util.Scanner;

/**
 * Launches the user interface responsible for the creation of the city on a certain map.
 */

public class CreateCityUI implements Runnable {
    /**
     * Controller handling city creation and validation.
     */
    private final CreateCityController ctrl = new CreateCityController();

    /**
     * Runs the "Create City" user interface.
     * <p>
     * Asks for city and map names,as well as coordinates for the city center, validates inputs, creates the city,
     * then allows manual or automatic block assignment.
     * </p>
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Create City ------------------------");
        Scanner sc = new Scanner(System.in);
        String cityName, mapName;

        String pattern = "^[A-Za-zÀ-Ÿà-ÿ ]+$";
        NameMapError nm;
            do {
                System.out.print("Enter City Name: ");
                cityName = sc.nextLine().trim();
                if (!cityName.matches(pattern)) {
                    System.out.println("Invalid city name. Please enter a valid name. No special characters nor digits are allowed.");
                }
            } while (!cityName.matches(pattern));
            MapRepository mrepo = Repositories.getInstance().getMapRepository();
            System.out.println(mrepo.getMaps());
            do {
                System.out.print("Enter Map Name: ");
                mapName = sc.nextLine().trim();
                nm = ctrl.validateNameMap(cityName, mapName);

                switch (nm) {
                    case MAP_NOT_FOUND:
                        System.out.println("Error: Map '" + mapName + "' does not exist.");
                        break;
                    case NAME_DUPLICATE:
                        System.out.println("Error: City name '" + cityName + "' already used on map '" + mapName + "'.");
                        break;
                    case OK:
                        // Successful validation
                        System.out.println("Map validated successfully!");
                        break;
                }
            } while (nm != NameMapError.OK);

            int cx = 0, cy = 0;
            CenterError ce;
            do {
                boolean validX = false;
                while (!validX) {
                    System.out.print("Enter Center X: ");
                    String input = sc.nextLine().trim();
                    try {
                        cx = Integer.parseInt(input);
                        validX = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: '" + input + "' is not a valid integer. Please enter a number.");
                    }
                }

                boolean validY = false;
                while (!validY) {
                    System.out.print("Enter Center Y: ");
                    String input = sc.nextLine().trim();
                    try {
                        cy = Integer.parseInt(input);
                        validY = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: '" + input + "' is not a valid integer. Please enter a number.");
                    }
                }

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
                int n = 0;
                boolean valid = false;
                while (!valid) {
                    System.out.print("How many blocks to add? ");
                    String input = sc.nextLine().trim();
                    try {
                        n = Integer.parseInt(input);
                        valid = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: '" + input + "' is not a valid integer. Please enter a number.");
                    }
                }
                for (int i = 0; i < n; i++) {
                    HouseBlock b;
                    BlockError be;
                    do {
                        System.out.print("Block " + (i + 1) + " X: ");
                        int bx = Integer.parseInt(sc.nextLine());
                        System.out.print("Block " + (i + 1) + " Y: ");
                        int by = Integer.parseInt(sc.nextLine());
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
                int n = 0;
                boolean validN = false;
                while (!validN) {
                    System.out.print("Number of blocks: ");
                    String input = sc.nextLine().trim();
                    try {
                        n = Integer.parseInt(input);
                        validN = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: '" + input + "' is not a valid integer. Please enter a number.");
                    }
                }

                double s = 0;
                boolean validS = false;
                while (!validS) {
                    System.out.print("Sigma (spread): ");
                    String input = sc.nextLine().trim();
                    try {
                        s = Double.parseDouble(input);
                        validS = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Error: '" + input + "' is not a valid number. Please enter a decimal.");
                    }
                }
                ctrl.assignBlocksAuto(city, n, s);
                System.out.println("Blocks assigned automatically.");
            }

            System.out.println("City created with success.");
        }
    }
