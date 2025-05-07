import controller.AuthController;
import domain.Budget;

import java.util.Optional;

public class Bootstrap {
    public void run() {
        addUsers();
    }

    private void addUsers() {
        //TODO: add Authentication users here: should be created for each user in the organization
        //get authentication repository
        AuthController authController = new AuthController();

        authController.createNewUser("Main Editor", "editor@this.app", "admin", authController.ROLE_EDITOR, Optional.empty());
        authController.createNewUser("Player 1", "Player@this.app", "pwd", authController.ROLE_PLAYER, Optional.of(new Budget(1000)));
    }
}
