package repository;

import domain.Map;
import domain.User;
import domain.CurrentUser;

import java.io.*;
import java.util.*;

/**
 * Repository class for managing user authentication and authorization
 * Handles user creation, login, logout, and role management operations
 */
public class AuthRepository {

    /** Collection of all users in the system */
    Set<User> users = new HashSet<>();

    /** Current user session for authentication context */
    CurrentUser session = CurrentUser.getInstance();

    /**
     * Creates a new user in the system if the email is not already in use
     * @param user The user object to add to the repository
     * @return true if user was successfully created, false if email already exists
     */
    public boolean createNewUser(User user) {
        if (!doesUserExist(user)) {
            users.add(user);
            return true;
        }
        return false;
    }

    /**
     * Checks if a password is usable for a user account
     * @param password The password to check for validity
     * @return true if password is usable, false otherwise
     */
    public boolean isPasswordUsable(String password) {
        int numOfNums = 0;
        int numOfCapitals = 0;
        if (password.length() < 7) {
            return false;
        }

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                numOfCapitals++;
            }
            if (Character.isDigit(c)) {
                numOfNums++;
            }
            // Check for special characters - if found, return false
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        if (numOfNums < 2) {
            return false;
        }

        if (numOfCapitals < 3) {
            return false;
        }

        return true;
    }

    /**
     * Gets all users registered in the system
     * @return List of all users
     */
    public List<User> getAllUsers(){

        return new ArrayList<>(users);
    }

    /**
     * Finds a user by their email address
     * @param email The email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserByEmail(String email) {

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if a user object already exists in the repository based on email
     * @param user The user to check for existence
     * @return true if a user with the same email exists, false otherwise
     */
    private boolean doesUserExist(User user){
        for (User user1 : users) {
            if (user1.getEmail().equalsIgnoreCase(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user with the specified email exists in the repository
     * @param email The email to check
     * @return true if a user with the email exists, false otherwise
     */
    public boolean doesUserExist(String email) {
        for (User user1 : users) {
            if (user1.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates a user's password against stored credentials
     * @param email The user's email address
     * @param password The password to validate
     * @return true if password is valid, false otherwise
     */
    private boolean isPasswordValid(String email, String password) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);
            String userPwd = "";

            if (usr.isPresent()) {
                userPwd = usr.get().getPassword();
            }

            return userPwd.equals(password);
        }else{
            return false;
        }
    }

    /**
     * Deletes a user from the system by email
     * @param email The email of the user to delete
     * @return true if user was successfully deleted, false if user not found
     */
    public boolean deleteUser(String email) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);

            usr.ifPresent(user -> users.remove(user));

            if (usr.isPresent() && session.getUser() != null && session.getUser().equals(usr.get())) {
                doLogout();
            }
            return true;
        }
        return false;
    }

    /**
     * Deletes a user from the system by user object
     * @param user The user object to delete
     * @return true if user was successfully deleted, false if user not found
     */
    public boolean deleteUser(User user) {
        if (doesUserExist(user.getEmail())) {
            users.remove(user);
            if (session.getUser() != null && session.getUser().equals(user)) {
                doLogout();
            }
            return true;
        }
        return false;
    }

    /**
     * Changes a user's role by email
     * @param email The email of the user
     * @param roleId The new role to assign
     * @return true if role was successfully changed, false if user not found
     */
    public boolean changeRole(String email, String roleId) {
        if (doesUserExist(email)) {
            Optional<User> usr = getUserByEmail(email);

            usr.ifPresent(user -> user.setRoleId(roleId));

            return true;
        }
        return false;
    }

    /**
     * Changes a user's role by user object
     * @param user The user object
     * @param roleId The new role to assign
     * @return true if role was successfully changed, false if user not found
     */
    public boolean changeRole(User user, String roleId) {
        if (doesUserExist(user)) {
            user.setRoleId(roleId);
            return true;
        }
        return false;
    }

    /**
     * Authenticates a user with email and password
     * @param email The user's email address
     * @param password The user's password
     * @return true if login was successful, false otherwise
     */
    public boolean doLogin(String email, String password) {
        if (isPasswordValid(email, password)){
            Optional<User> usr = getUserByEmail(email);
            return session.doLogin(usr.get());
        }
        return false;
    }

    /**
     * Logs out the currently authenticated user
     * @return true indicating successful logout
     */
    public boolean doLogout() {
        session.doLogout();
        return true;
    }

    /**
     * Gets the role of a specific user by email
     * @param email The email of the user
     * @return The role of the specified user
     */
    public String getRole(String email) {
        Optional<User> usr = getUserByEmail(email);
        return usr.get().getRoleId();
    }

    /**
     * Gets the email of the currently authenticated user
     * @return Email address of the current user
     */
    public String getCurrentUserEmail() {
        return session.getUser().getEmail();
    }

    /**
     * Saves all users to a file called users.usr
     */
    public void saveUserToFile(){
        File authFile = new File("users.usr");
        try (FileOutputStream fos = new FileOutputStream(authFile)){
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(users);
        }catch(IOException e){
            System.err.println("Error saving Users");
        }
    }

    /**
     * Loads all users from a file called users.usr
     * @return list of users from the file, or empty list if file does not exist
     */
    public Set<User> getUsersFromFile(){
        Set<User> savedUsers = new HashSet<>();
        File usersFile = new File("users.usr");
        if (usersFile.exists()) {

            try (FileInputStream fis = new FileInputStream(usersFile)) {
                ObjectInputStream ois = new ObjectInputStream(fis);

                Object obj = ois.readObject();
                if (obj instanceof Set<?>) {
                    Set<?> loadedUsers = (Set<?>) obj;
                    savedUsers.clear();

                    for (Object loadedUser : loadedUsers) {
                        if (loadedUser instanceof User) {
                            savedUsers.add((User) loadedUser);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading Users");
            } catch (ClassNotFoundException e) {
                System.err.println("Error loading Users");
            }
        }
        return savedUsers;
    }

    /**
     * Checks if an email is usable for a user account
     * @param email The email to check for validity
     * @return true if email is usable, false otherwise
     */
    public boolean isEmailUsable(String email){
        if (email.split("@").length > 2) {
            return false;
        }
        return true;
    }

}