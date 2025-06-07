package ui.console;

import controller.AuthController;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import ui.console.menu.MainMenuUI;
import ui.gui.PlayMenuGUI;


public class AuthUI implements Runnable {
    private final AuthController ctrl;
    private final int op;

    public AuthUI(int op){
        ctrl = new AuthController();
        this.op = op;
    }

    public void createUser(){
        System.out.println("\n\n--- REGISTRATION UI ---------------------------");
        boolean success;
        do {

            String name = Utils.readLineFromConsole("Enter your name: ");
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            String pwd = Utils.readLineFromConsole("Enter Password (Include at least 7 characters, including at least 2 numbers and 3 capital letters): ");
            String roleID = AuthController.ROLE_PLAYER;

            success = ctrl.createNewUser(name, id, pwd, roleID);
            if (!success) {
                System.out.println("User already exists or password doesn't meet the requirements");
            }

        } while (!success);
        ctrl.saveUserToFile();
        System.out.println("User created successfully");
    }

    public void run() {
        switch(op){
            case 1:
                try {
                    run1();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                createUser();
                break;
        }
    }

    public void run1() throws Exception {
        boolean success = doLogin();
        if (success) {
            if (ctrl.getRole(ctrl.getCurrentUserEmail()).equals(AuthController.ROLE_EDITOR)) {
                MainMenuUI admUI = new MainMenuUI();
                admUI.run();
            }else{
                //UserMenuUI menu = new UserMenuUI();
                PlayMenuGUI menu = new PlayMenuGUI();
                menu.launchApp();
            }
        }
        this.logout();
    }

    private boolean doLogin() {
        System.out.println("\n\n--- LOGIN UI ---------------------------");

        int maxAttempts = 3;
        boolean success = false;
        do {
            String id = Utils.readLineFromConsole("Enter UserId/Email: ");
            if (ctrl.doesUserExist(id)){
                String pwd = Utils.readLineFromConsole("Enter Password: ");

                success = ctrl.doLogin(id, pwd);
                if (!success) {
                    maxAttempts--;
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
