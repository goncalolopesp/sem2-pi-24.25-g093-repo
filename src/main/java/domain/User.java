package domain;

import java.io.Serializable;
import java.util.*;

/**
 * User entity representing a player or editor in the system
 * Stores user identification, authentication and role information
 */
public class User implements Serializable {

    /** User's full name */
    String name;

    /** User's email address (used as unique identifier) */
    String email;

    /** User's password for authentication */
    String password;

    /** Role identifier (PLAYER or EDITOR) */
    String roleId;

    /** List of locomotives owned by this user */
    List<Locomotive> locomotives;

    /**
     * Creates a new User with the specified credentials and role
     * @param name The user's full name
     * @param email The user's email address
     * @param password The user's password
     * @param roleId The role assigned to this user (PLAYER or EDITOR)
     */
    public User(String name, String email, String password, String roleId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
        this.locomotives = new ArrayList<>();
    }

    /**
     * Gets the user's name
     * @return The user's full name
     */
    public String getName() { return name; }

    /**
     * Gets the user's email address
     * @return The user's email
     */
    public String getEmail() { return email; }

    /**
     * Gets the user's password
     * @return The user's password
     */
    public String getPassword() { return password; }

    /**
     * Gets the user's role identifier
     * @return The user's role (PLAYER or EDITOR)
     */
    public String getRoleId() { return roleId; }

    /**
     * Gets the list of locomotives owned by this user
     * @return List of user's locomotives
     */
    public List<Locomotive> getLocomotives() { return locomotives; }

    /**
     * Sets the user's name
     * @param name The new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the user's email address
     * @param email The new email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Sets the user's password
     * @param password The new password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Sets the user's role identifier
     * @param roleId The new role (PLAYER or EDITOR)
     */
    public void setRoleId(String roleId) { this.roleId = roleId; }

    /**
     * Sets the list of locomotives owned by this user
     * @param locomotives The new list of locomotives
     */
    public void setLocomotives(List<Locomotive> locomotives) { this.locomotives = locomotives; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}