package ui.console.menu;

import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.*;

import java.util.ArrayList;
import java.util.List;

public class UserMenuUI {

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();
        options.add(new MenuItem("Play Game", new AuthUI(1)));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PLAYER MENU --------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
