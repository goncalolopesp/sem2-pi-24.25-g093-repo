package ui.console.menu;


import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.CreateMapUI;
import ui.console.CreateCityUI;
import ui.console.CreateScenarioUI;
import ui.console.DisplayMapUI;

import java.util.ArrayList;
import java.util.List;

public class MainMenuUI implements Runnable{
    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Create Map", new CreateMapUI()));
        options.add(new MenuItem("Create City", new CreateCityUI()));
        options.add(new MenuItem("Create Scenario", new CreateScenarioUI()));
        options.add(new MenuItem("Display Map", new DisplayMapUI()));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
