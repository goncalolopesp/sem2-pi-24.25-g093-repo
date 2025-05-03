package ui.console;

import controller.CreateMapController;
import domain.Map;

import java.util.Optional;
import java.util.Scanner;

public class CreateMapUI implements Runnable{
    private final CreateMapController ctrl;
    private String mapName;
    private int width;
    private int height;

    public CreateMapUI() { ctrl = new CreateMapController(); }

    private CreateMapController getMapController() { return ctrl; }

    @Override
    public void run() {
        System.out.println("\n\n--- Create Map ------------------------");
        requestData();
        submitData();
    }

    private void submitData() {
        Optional<Map> map = Optional.ofNullable(getMapController().createMap(mapName, width, height));

        if (map.isPresent()) {
            System.out.println("\nMap successfully created!");
        } else {
            System.out.println("\nMap not created!");
        }
    }

    private void requestData() {
        mapName = requestMapName();
        width = requestWidth();
        height = requestHeight();
    }

    private int requestHeight() {
        Scanner sc = new Scanner(System.in);
        int h;

        System.out.print("Enter Map Height: ");

        do {
            h = sc.nextInt();
            if (h <= 0 || h > 256) {
                System.out.print("Invalid height. Must be positive and below 256. Try again: ");
            }
        } while (h <= 0 || h > 256);

        return h;
    }

    private int requestWidth() {
        Scanner sc = new Scanner(System.in);
        int w;

        System.out.print("Enter Map Width: ");

        do {
            w = sc.nextInt();
            if (w <= 0 || w > 256) {
                System.out.print("Invalid width. Must be positive and below 256. Try again: ");
            }
        } while (w <= 0 || w > 256);

        return w;
    }

    private String requestMapName() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Map Name: ");
        return sc.nextLine().trim();
    }
}
