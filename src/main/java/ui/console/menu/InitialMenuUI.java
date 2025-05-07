package ui.console.menu;

import pt.ipp.isep.dei.ui.console.authorization.AuthenticationUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.*;

import java.util.ArrayList;
import java.util.List;

public class InitialMenuUI {

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Do Login", new AuthUI(1)));
        options.add(new MenuItem("Create user", new AuthUI(2)));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- MAIN MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

}
