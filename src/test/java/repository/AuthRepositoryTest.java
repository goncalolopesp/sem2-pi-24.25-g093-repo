package repository;

import domain.CurrentUser;
import domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class AuthRepositoryTest {

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

    private final String TEST_FILE = "test_users.usr";

    @BeforeEach
    void setUp() {
        CurrentUser.getInstance().doLogout();

        authRepository = new AuthRepository();
        testUser = new User(userName, userEmail, userPassword, userRole);
        anotherUser = new User(anotherName, anotherEmail, anotherPassword, anotherRole);
        currentUser = CurrentUser.getInstance();

        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void testIsPasswordUsable() {
        assertFalse(authRepository.isPasswordUsable("Abc12"));
        assertFalse(authRepository.isPasswordUsable(""));
        assertFalse(authRepository.isPasswordUsable("ABCDEFGh"));
        assertFalse(authRepository.isPasswordUsable("abcdefg12"));
        assertFalse(authRepository.isPasswordUsable("ABC123def!"));
        assertTrue(authRepository.isPasswordUsable("ABC123def"));
        assertTrue(authRepository.isPasswordUsable("ABC12de"));
    }

    @Test
    void testIsPasswordValid_UserDoesNotExist() {
        assertFalse(authRepository.doLogin("nonexistent@email.com", "password"));
        User testUserForPassword = new User("Test", "test@email.com", "correctPassword", "user");
        authRepository.createNewUser(testUserForPassword);

        assertTrue(authRepository.doesUserExist("test@email.com"));
        assertEquals("correctPassword", authRepository.getUserByEmail("test@email.com").get().getPassword());
    }

    @Test
    void testSaveUserToFile_Success() {
        authRepository.createNewUser(testUser);
        authRepository.createNewUser(anotherUser);

        AuthRepository testRepo = new AuthRepository() {
            @Override
            public void saveUserToFile() {
                File authFile = new File(TEST_FILE);
                try (FileOutputStream fos = new FileOutputStream(authFile)){
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(users);
                }catch(IOException e){
                    System.err.println("Error saving Users");
                }
            }
        };
        testRepo.users = authRepository.users;

        testRepo.saveUserToFile();

        File testFile = new File(TEST_FILE);
        assertTrue(testFile.exists());
        assertTrue(testFile.length() > 0);

        testFile.delete();
    }

    @Test
    void testGetUsersFromFile_Success() throws IOException {
        Set<User> originalUsers = new HashSet<>();
        User user1 = new User("User1", "user1@test.com", "password1", "admin");
        User user2 = new User("User2", "user2@test.com", "password2", "user");
        originalUsers.add(user1);
        originalUsers.add(user2);

        File testFile = new File(TEST_FILE);
        try (FileOutputStream fos = new FileOutputStream(testFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(originalUsers);
        }

        AuthRepository testRepo = new AuthRepository() {
            @Override
            public Set<User> getUsersFromFile() {
                Set<User> savedUsers = new HashSet<>();
                File usersFile = new File(TEST_FILE);
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
        };

        Set<User> loadedUsers = testRepo.getUsersFromFile();

        assertEquals(originalUsers.size(), loadedUsers.size());
        assertEquals(originalUsers, loadedUsers);

        testFile.delete();
    }

    @Test
    void testIsEmailUsable_MultipleAtSymbols() {
        assertFalse(authRepository.isEmailUsable("user@@domain.com"));
        assertFalse(authRepository.isEmailUsable("user@domain@com"));
        assertTrue(authRepository.isEmailUsable("user@domain.com"));
        assertTrue(authRepository.isEmailUsable("test.user@example.org"));
        assertTrue(authRepository.isEmailUsable(""));
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