package controller;

import domain.Budget;
import domain.User;
import repository.AuthRepository;
import repository.Repositories;

import java.util.*;

/**
 * Controller class responsible for handling authentication and user management operations
 * Acts as an intermediary between the UI and the authentication repository
 */
public class AuthController {

    /** Repository for authentication and user operations */
    AuthRepository authRepo = new AuthRepository();

    /** Constant representing the player role */
    public static final String ROLE_PLAYER = "PLAYER";

    /** Constant representing the editor role */
    public static final String ROLE_EDITOR = "EDITOR";

    /**
     * Constructor initializes the authentication repository from the singleton repositories instance
     */
    public AuthController() {
        authRepo = Repositories.getInstance().getAuthRepository();
    }

    /**
     * Creates a new user in the system
     * @param name The user's full name
     * @param email The user's email address (used as unique identifier)
     * @param pwd The user's password
     * @param roleId The role assigned to the user (PLAYER or EDITOR)
     * @return true if user was successfully created, false if creation failed
     */
    public boolean createNewUser(String name, String email, String pwd, String roleId){
        User user = new User(name, email, pwd, roleId);
        return authRepo.createNewUser(user);
    }

    /**
     * Authenticates a user with email and password
     * @param email The user's email address
     * @param password The user's password
     * @return true if login was successful, false otherwise
     */
    public boolean doLogin(String email, String password) {
        return authRepo.doLogin(email, password);
    }

    /**
     * Logs out the currently authenticated user
     * @return true if logout was successful, false otherwise
     */
    public boolean doLogout() {
        return authRepo.doLogout();
    }

    /**
     * Retrieves all users registered in the system
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return authRepo.getAllUsers();
    }

    /**
     * Deletes a user from the system
     * @param email The email address of the user to delete
     * @return true if user was successfully deleted, false otherwise
     */
    public boolean deleteUser(String email) {
        return authRepo.deleteUser(email);
    }

    /**
     * Gets the role of a specific user
     * @param email The email address of the user
     * @return The role of the user (PLAYER or EDITOR)
     */
    public String getRole(String email) {
        return authRepo.getRole(email);
    }

    /**
     * Gets the email of the currently authenticated user
     * @return Email address of the current user
     */
    public String getCurrentUserEmail() {
        return authRepo.getCurrentUserEmail();
    }

    /**
     * Checks if a user with the specified email exists
     * @param email The email address to check
     * @return true if user exists, false otherwise
     */
    public boolean doesUserExist(String email) {
        return authRepo.doesUserExist(email);
    }
}