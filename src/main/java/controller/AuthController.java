package controller;

import domain.Budget;

import domain.User;
import repository.AuthRepository;
import repository.Repositories;

import java.util.*;

public class AuthController {

    AuthRepository authRepo = new AuthRepository();
    public static final String ROLE_PLAYER = "PLAYER";
    public static final String ROLE_EDITOR = "EDITOR";

    public AuthController() {
        authRepo = Repositories.getInstance().getAuthRepository();
    }


    public boolean createNewUser(String name, String email, String pwd, String roleId, Optional<Budget> budget){
        Budget budget1;
        if (budget.isPresent()) {
            budget1 = budget.get();
        }else{
            budget1 = new Budget(0);
        }
        User user = new User(name, email, pwd, roleId, budget1);
        return authRepo.createNewUser(user);
    }

    public boolean doLogin(String email, String password) {
        return authRepo.doLogin(email, password);
    }

    public boolean doLogout() {
        return authRepo.doLogout();
    }

    public List<User> getAllUsers() {
        return authRepo.getAllUsers();
    }

    public boolean deleteUser(String email) {
        return authRepo.deleteUser(email);
    }

    public String getRole(String email) {
        return authRepo.getRole(email);
    }

    public String getCurrentUserEmail() {
        return authRepo.getCurrentUserEmail();
    }

}
