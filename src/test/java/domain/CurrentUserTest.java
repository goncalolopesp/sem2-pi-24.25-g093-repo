package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CurrentUserTest {
    private CurrentUser currentUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        currentUser = CurrentUser.getInstance();
        currentUser.clear();
        testUser = new User("Test User", "test@example.com", "password", "PLAYER");
    }

    @Test
    void getInstance() {
        CurrentUser instance1 = CurrentUser.getInstance();
        CurrentUser instance2 = CurrentUser.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    void getUserAndSetUser() {
        assertNull(currentUser.getUser());

        currentUser.setUser(testUser);
        assertEquals(testUser, currentUser.getUser());
    }

    @Test
    void doLogin() {
        boolean loginResult = currentUser.doLogin(testUser);

        assertTrue(loginResult);
        assertEquals(testUser, currentUser.getUser());
    }

    @Test
    void clear() {
        currentUser.setUser(testUser);
        assertNotNull(currentUser.getUser());

        currentUser.clear();
        assertNull(currentUser.getUser());
    }

    @Test
    void doLogout() {
        currentUser.setUser(testUser);
        assertNotNull(currentUser.getUser());

        boolean logoutResult = currentUser.doLogout();

        assertTrue(logoutResult);
        assertNull(currentUser.getUser());
    }
}