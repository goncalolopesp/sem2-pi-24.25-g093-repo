package domain;

import repository.LocomotiveRepository;

import java.util.Map;
import java.util.HashMap;

/**
 * Singleton class that manages the currently authenticated user
 * Provides a global access point for the current user session throughout the application
 */
public class CurrentUser {
    /** The single instance of CurrentUser (Singleton pattern) */
    private static CurrentUser session = null;

    /** Map to store the current user - using a map with a constant key for flexibility */
    private Map<String, User> usr;

    /**
     * Private constructor prevents instantiation from outside
     * Initializes the user map
     */
    private CurrentUser() {
        usr = new HashMap<>();
    }

    /**
     * Gets the singleton instance of CurrentUser, creating it if needed
     * @return The singleton CurrentUser instance
     */
    public static CurrentUser getInstance() {
        if (session == null) {
            session = new CurrentUser();
        }
        return session;
    }

    /**
     * Gets the currently authenticated user
     * @return The current user or null if no user is authenticated
     */
    public User getUser() {
        return usr.get("currentUser");
    }

    /**
     * Sets the currently authenticated user
     * @param user The user to set as current
     */
    public void setUser(User user) {
        usr.put("currentUser", user);
    }

    /**
     * Logs in a user by setting them as the current user
     * @param user The user to log in
     * @return true indicating successful login
     */
    public boolean doLogin(User user) {
        setUser(user);
        return true;
    }

    /**
     * Clears all user data from the session
     */
    public void clear(){
        usr.clear();
    }

    /**
     * Logs out the current user by clearing the session
     * @return true indicating successful logout
     */
    public boolean doLogout() {
        clear();
        return true;
    }
}