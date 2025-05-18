package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;
    private final String name = "Test User";
    private final String email = "test@example.com";
    private final String password = "password123";
    private final String roleId = "PLAYER";

    @BeforeEach
    void setUp() {
        user = new User(name, email, password, roleId);
    }

    @Test
    void getName() {
        assertEquals(name, user.getName());
    }

    @Test
    void setName() {
        String newName = "Updated User";
        user.setName(newName);
        assertEquals(newName, user.getName());
    }

    @Test
    void getEmail() {
        assertEquals(email, user.getEmail());
    }

    @Test
    void setEmail() {
        String newEmail = "updated@example.com";
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals(password, user.getPassword());
    }

    @Test
    void setPassword() {
        String newPassword = "newSecurePassword";
        user.setPassword(newPassword);
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void getRoleId() {
        assertEquals(roleId, user.getRoleId());
    }

    @Test
    void setRoleId() {
        String newRoleId = "EDITOR";
        user.setRoleId(newRoleId);
        assertEquals(newRoleId, user.getRoleId());
    }

    @Test
    void getLocomotives() {
        assertNotNull(user.getLocomotives());
        assertTrue(user.getLocomotives().isEmpty());
    }

    @Test
    void setLocomotives() {
        List<Locomotive> locomotives = new ArrayList<>();
        locomotives.add(new Locomotive("Model1", 1000, "Steam", 100, 50, 1900, 1950, 80));
        locomotives.add(new Locomotive("Model2", 2000, "Diesel", 80, 30, 1940, 1980, 120));

        user.setLocomotives(locomotives);

        assertEquals(locomotives, user.getLocomotives());
        assertEquals(2, user.getLocomotives().size());
    }

    @Test
    void constructorInitializesEmptyLocomotivesList() {
        assertNotNull(user.getLocomotives());
        assertEquals(0, user.getLocomotives().size());
    }
}