package controller;

import domain.Budget;

import domain.User;
import repository.AuthRepository;
import repository.Repositories;

import java.util.*;

public class AuthController {

    AuthRepository authRepo;
    public static final String ROLE_PLAYER = "PLAYER";
    public static final String ROLE_EDITOR = "EDITOR";

    public AuthController() {
        authRepo = Repositories.getInstance().getAuthRepository();
    }


    public boolean createNewUser(String name, String email, String pwd, String roleId){
        User user = new User(name, email, pwd, roleId);
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

    public boolean doesUserExist(String email) {
        return authRepo.doesUserExist(email);
    }

}
