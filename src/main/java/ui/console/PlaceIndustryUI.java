package ui.console;

import controller.PlaceIndustryController;
import domain.Industry;
import domain.Map;

import java.util.Scanner;

public class PlaceIndustryUI implements Runnable {
    /** Controller responsible for map creation logic. */
    private final PlaceIndustryController ctrl;
    private Map map;
    private int positionX;
    private int positionY;
    private Industry industry;

    public PlaceIndustryUI() {
        ctrl = new PlaceIndustryController();
        industry = null;
    }


    private PlaceIndustryController getIndustryController() { return ctrl; }


    @Override
    public void run() {
        System.out.println("\n\n--- Place Industry On Map ------------------------");
        requestData();
        submitData();
    }


    private void submitData() {
        if (ctrl.placeIndustryOnMap(industry, positionX, positionY, map)) {
            System.out.println("Industry placed successfully.");
        } else {
            System.out.println("Failed to place industry. Position may be occupied or invalid.");
        }
    }


    private void requestData() {
        map = selectMap();
        positionX = requestPositionX();
        positionY = requestPositionY();
        industry = selectIndustry();
    }


    private int requestPositionY() {
        Scanner sc = new Scanner(System.in);
        int y;

        System.out.print("Enter Industry position Y: ");

        do {
            y = sc.nextInt();
            if (y <= 0 || y > 256) {
                System.out.print("Invalid position. Must be positive and below 256. Try again: ");
            }
        } while (y <= 0 || y > 256);

        return y;
    }


    private int requestPositionX() {
        Scanner sc = new Scanner(System.in);
        int x;

        System.out.print("Enter Industry position X: ");

        do {
            x = sc.nextInt();
            if (x <= 0 || x > 256) {
                System.out.print("Invalid position. Must be positive and below 256. Try again: ");
            }
        } while (x <= 0 || x > 256);

        return x;
    }


    private Industry selectIndustry() {
        for (Industry industry : getIndustryController().getIndustries()) {
            System.out.println(industry.getType());
        }

        Scanner sc = new Scanner(System.in);
        int i;

        System.out.print("Select Industry: ");

        do {
            i = sc.nextInt();
            if (i < 0 || i >= getIndustryController().getIndustries().size()) {
                System.out.print("Invalid selection. Try again: ");
            }
        } while (i < 0 || i >= getIndustryController().getIndustries().size());

        return getIndustryController().getIndustries().get(i);
    }

    private Map selectMap() {
        for (Map map : getIndustryController().getMaps()) {
            System.out.println(map.getMapName());
        }

        Scanner sc = new Scanner(System.in);
        int i;

        System.out.print("Select Map: ");

        do {
            i = sc.nextInt();
            if (i < 0 || i >= getIndustryController().getMaps().size()) {
                System.out.print("Invalid selection. Try again: ");
            }
        } while (i < 0 || i >= getIndustryController().getMaps().size());

        return getIndustryController().getMaps().get(i);

    }
}
