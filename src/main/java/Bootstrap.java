import controller.AuthController;
import controller.CreateMapController;
import controller.CreateScenarioController;
import domain.Budget;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bootstrap {
    public void run() {
        addUsers(); addMaps(); addScenarios();
    }

    private void addUsers() {
        //TODO: add Authentication users here: should be created for each user in the organization
        //get authentication repository
        AuthController authController = new AuthController();

        authController.createNewUser("Main Editor", "editor@this.app", "admin", authController.ROLE_EDITOR, Optional.empty());
        authController.createNewUser("Player 1", "Player@this.app", "pwd", authController.ROLE_PLAYER, Optional.of(new Budget(1000)));
    }

    private void addMaps() {
        CreateMapController createMapController = new CreateMapController();
        createMapController.createMap("exampleMap", 50, 50);
    }

    private void addScenarios() {
        CreateScenarioController createScenarioController = new CreateScenarioController();
        int[] timeRestrictions = {1974, 1978};
        List<String> historicalRestrictions = new ArrayList<>();
        historicalRestrictions.add("none");
        List<String> techRestrictions = new ArrayList<>();
        techRestrictions.add("none");
        createScenarioController.createScenario("exampleScenario", "exampleMap", timeRestrictions, techRestrictions, historicalRestrictions);
    }

}