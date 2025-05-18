package repository;

import domain.CurrentUser;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthRepositoryTest {
    private AuthRepository authRepository;
    private User testUser;
    private User anotherUser;
    private CurrentUser currentUser;

    private final String userName = "Test User";
    private final String userEmail = "test@example.com";
    private final String userPassword = "password123";
    private final String userRole = "PLAYER";

    private final String anotherName = "Another User";
    private final String anotherEmail = "another@example.com";
    private final String anotherPassword = "password456";
    private final String anotherRole = "EDITOR";

    @BeforeEach
    void setUp() {
        CurrentUser.getInstance().doLogout();

        authRepository = new AuthRepository();
        testUser = new User(userName, userEmail, userPassword, userRole);
        anotherUser = new User(anotherName, anotherEmail, anotherPassword, anotherRole);
        currentUser = CurrentUser.getInstance();
    }

    @Test
    void createNewUser() {
        assertTrue(authRepository.createNewUser(testUser));

        User duplicateUser = new User("Duplicate", userEmail, "differentpassword", "PLAYER");
        assertFalse(authRepository.createNewUser(duplicateUser));
    }

    @Test
    void getAllUsers() {
        List<User> emptyList = authRepository.getAllUsers();
        assertTrue(emptyList.isEmpty());

        authRepository.createNewUser(testUser);
        authRepository.createNewUser(anotherUser);

        List<User> userList = authRepository.getAllUsers();
        assertEquals(2, userList.size());
        assertTrue(userList.contains(testUser));
        assertTrue(userList.contains(anotherUser));
    }

    @Test
    void getUserByEmail() {
        Optional<User> notFound = authRepository.getUserByEmail(userEmail);
        assertFalse(notFound.isPresent());

        authRepository.createNewUser(testUser);
        Optional<User> found = authRepository.getUserByEmail(userEmail);

        assertTrue(found.isPresent());
        assertEquals(testUser, found.get());
    }

    @Test
    void doesUserExist_withString() {
        assertFalse(authRepository.doesUserExist(userEmail));

        authRepository.createNewUser(testUser);
        assertTrue(authRepository.doesUserExist(userEmail));
    }

    @Test
    void deleteUser_withString() {
        authRepository.createNewUser(testUser);
        assertTrue(authRepository.doesUserExist(userEmail));

        assertTrue(authRepository.deleteUser(userEmail));
        assertFalse(authRepository.doesUserExist(userEmail));

        assertFalse(authRepository.deleteUser("nonexistent@example.com"));
    }

    @Test
    void deleteUser_withUser() {
        authRepository.createNewUser(testUser);
        assertTrue(authRepository.doesUserExist(userEmail));

        assertTrue(authRepository.deleteUser(testUser));
        assertFalse(authRepository.doesUserExist(userEmail));

        User nonExistentUser = new User("Non", "nonexistent@example.com", "pwd", "PLAYER");
        assertFalse(authRepository.deleteUser(nonExistentUser));
    }

    @Test
    void changeRole_withString() {
        authRepository.createNewUser(testUser);

        String newRole = "EDITOR";
        assertTrue(authRepository.changeRole(userEmail, newRole));
        assertEquals(newRole, authRepository.getRole(userEmail));

        assertFalse(authRepository.changeRole("nonexistent@example.com", newRole));
    }

    @Test
    void changeRole_withUser() {
        authRepository.createNewUser(testUser);

        String newRole = "EDITOR";
        assertTrue(authRepository.changeRole(testUser, newRole));
        assertEquals(newRole, authRepository.getRole(userEmail));

        User nonExistentUser = new User("Non", "nonexistent@example.com", "pwd", "PLAYER");
        assertFalse(authRepository.changeRole(nonExistentUser, newRole));
    }

    @Test
    void doLogin() {
        authRepository.createNewUser(testUser);

        assertTrue(authRepository.doLogin(userEmail, userPassword));
        assertEquals(testUser, currentUser.getUser());

        authRepository.doLogout();
        assertFalse(authRepository.doLogin(userEmail, "wrongPassword"));
        assertNull(currentUser.getUser());

        assertFalse(authRepository.doLogin("nonexistent@example.com", userPassword));
    }

    @Test
    void doLogout() {
        authRepository.createNewUser(testUser);
        authRepository.doLogin(userEmail, userPassword);
        assertNotNull(currentUser.getUser());

        assertTrue(authRepository.doLogout());
        assertNull(currentUser.getUser());
    }

    @Test
    void getRole() {
        authRepository.createNewUser(testUser);

        assertEquals(userRole, authRepository.getRole(userEmail));
    }

    @Test
    void getCurrentUserEmail() {
        authRepository.createNewUser(testUser);
        authRepository.doLogin(userEmail, userPassword);

        assertEquals(userEmail, authRepository.getCurrentUserEmail());
    }

    @Test
    void deleteLoggedInUser() {
        authRepository.createNewUser(testUser);
        authRepository.doLogin(userEmail, userPassword);
        assertNotNull(currentUser.getUser());

        assertTrue(authRepository.deleteUser(userEmail));

        assertNull(currentUser.getUser());
    }

    @Test
    void deleteLoggedInUserWithUserObject() {
        authRepository.createNewUser(testUser);
        authRepository.doLogin(userEmail, userPassword);
        assertNotNull(currentUser.getUser());

        assertTrue(authRepository.deleteUser(testUser));

        assertNull(currentUser.getUser());
    }
}