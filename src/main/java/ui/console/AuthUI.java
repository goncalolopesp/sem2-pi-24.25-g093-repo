package ui.console;

import controller.AuthController;
import domain.Budget;
import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.ui.console.menu.AdminUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.menu.MainMenuUI;
import ui.console.menu.UserMenuUI;

import java.util.Optional;

public class AuthUI implements Runnable {
    private final AuthController ctrl;
    private int op;

    public AuthUI(int op){
        ctrl = new AuthController();
        this.op = op;
    }

    public void createUser(){
        System.out.println("\n\n--- REGISTRATION UI ---------------------------");

        boolean success = false;
        do {

            String name = Utils.readLineFromConsole("Enter your name: ");
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            String pwd = Utils.readLineFromConsole("Enter Password: ");
            String roleID = ctrl.ROLE_PLAYER;
            Optional<Budget> budget = Optional.of(new Budget(1000));

            success = ctrl.createNewUser(name, id, pwd, roleID, budget);
            if (!success) {
                System.out.println("User already exists!");
            }

        } while (!success);
    }

    public void run() {
        switch(op){
            case 1:
                run1();
                break;
            case 2:
                createUser();
                break;
        }
    }

    public void run1(){
        boolean success = doLogin();
        if (success) {
            if (ctrl.getRole(ctrl.getCurrentUserEmail()).equals(AuthController.ROLE_EDITOR)) {
                MainMenuUI admUI = new MainMenuUI();
                admUI.run();
            }else{
                UserMenuUI menu = new UserMenuUI();
                menu.run();
            }
        }
        this.logout();
    }

    private boolean doLogin() {
        System.out.println("\n\n--- LOGIN UI ---------------------------");

        int maxAttempts = 3;
        boolean success = false;
        do {
            maxAttempts--;
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            if (ctrl.doesUserExist(id)){
                String pwd = Utils.readLineFromConsole("Enter Password: ");

                success = ctrl.doLogin(id, pwd);
                if (!success) {
                    System.out.println("Invalid UserId and/or Password. \n You have  " + maxAttempts + " more attempt(s).");
                }
            }else{
                System.out.println("User does not exist");
            }

        } while (!success && maxAttempts > 0);
        return success;
    }

    private void logout() {
        ctrl.doLogout();
    }

}
