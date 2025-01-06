package listenify.persistence;

import listenify.business.User;
import org.junit.jupiter.api.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {

    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() {
        // Initialize the UserDaoImpl with a test database
        userDao = new UserDaoImpl("database_test.properties");
    }

    @Test
    void testLogin() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "test01";
        String password = "";

        // Assuming the test user already exists in the database
        User user = userDao.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    @Test
    void testRegister() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        User user = new User(0, "newUser", "newPassword", "", "newuser@example.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1));
        boolean isRegistered = userDao.register(user);
        assertTrue(isRegistered);

        // Check if the user can now login
        User loggedInUser = userDao.login(user.getUsername(), user.getPassword());
        assertNotNull(loggedInUser);
        assertEquals(user.getUsername(), loggedInUser.getUsername());
    }

    @Test
    void testUpdateUser() throws SQLException {
        User user = new User(1, "updatedUser", "", "", "updateduser@example.com", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1));
        boolean isUpdated = userDao.updateUser(user);
        assertTrue(isUpdated);

        User updatedUser = userDao.getUserById(user.getUserId());
        assertNotNull(updatedUser);
        assertEquals(user.getUsername(), updatedUser.getUsername());
    }

    @Test
    void testUpdatePassword() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        int userId = 1;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";

        boolean isPasswordUpdated = userDao.updatePassword(userId, currentPassword, newPassword);
        assertTrue(isPasswordUpdated);

        // Verify the user can login with the new password
        User user = userDao.login("testUser", newPassword);
        assertNotNull(user);
    }

    @Test
    void testRenewSubscription() throws SQLException {
        int userId = 1;
        LocalDate currentDate = LocalDate.now();

        boolean isRenewed = userDao.renewSubscription(userId, currentDate);
        assertTrue(isRenewed);

        User user = userDao.getUserById(userId);
        assertNotNull(user);
        assertTrue(user.getSubscriptionEndDate().isAfter(currentDate));
    }

    @Test
    void testGetUserById() throws SQLException {
        int userId = 1;

        User user = userDao.getUserById(userId);
        assertNotNull(user);
        assertEquals(userId, user.getUserId());
    }

    @AfterEach
    void tearDown() {
        // Cleanup resources if necessary
    }
}
