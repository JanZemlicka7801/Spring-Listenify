package listenify.persistence;

import listenify.business.User;
import listenify.config.passwordHash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
public class UserDaoImpl extends MySQLDao implements UserDao {

    /**
     * Creates UserDaoImpl with specified database properties.
     *
     * @param dbName Database properties filename
     */
    public UserDaoImpl(String dbName) {
        super(dbName);
    }

    /**
     * Creates UserDaoImpl with default database properties.
     */
    public UserDaoImpl() {
        super();
    }


    @Override
    public List<User> getAllUsers() throws SQLException {
        return null;
    }

    /**
     * Authenticates user login attempt.
     *
     * @param username Username to check
     * @param password Password to verify
     * @return User object if login successful, null otherwise
     * @throws SQLException If database error occurs
     * @throws NoSuchAlgorithmException If hashing algorithm not found
     * @throws InvalidKeySpecException If password hashing fails
     */
    @Override
    public User login(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            if (conn == null) {
                throw new SQLException("Unable to connect to the database!");
            }

            String query = "SELECT * FROM Users WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String storedSalt = rs.getString("salt");
                String computedHash = passwordHash.hashPassword(password, storedSalt);

                if (computedHash.equals(storedHash)) {
                    LocalDate regDate = rs.getDate("registration_date").toLocalDate();
                    Date subStartDate = rs.getDate("subscription_start_date");
                    Date subEndDate = rs.getDate("subscription_end_date");

                    LocalDate startDate = subStartDate != null ? subStartDate.toLocalDate() : regDate;
                    LocalDate endDate = subEndDate != null ? subEndDate.toLocalDate() : regDate.plusYears(1);

                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            storedHash,
                            storedSalt,
                            rs.getString("email"),
                            regDate,
                            startDate,
                            endDate
                    );
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        return user;
    }

    /**
     * Registers a new user in the system.
     * Checks for duplicate username/email and hashes password before storage.
     *
     * @param user User object containing registration details
     * @return true if registration successful
     * @throws SQLException If database error occurs
     * @throws NoSuchAlgorithmException If hashing algorithm not found
     * @throws InvalidKeySpecException If password hashing fails
     */
    @Override
    public boolean register(User user) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            if (conn == null) {
                throw new SQLException("Unable to connect to the database!");
            }

            String checkUsernameQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";
            stmt = conn.prepareStatement(checkUsernameQuery);
            stmt.setString(1, user.getUsername());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Username already exists. Please choose a different username.");
                return false;
            }

            String checkEmailQuery = "SELECT COUNT(*) FROM Users WHERE email = ?";
            stmt = conn.prepareStatement(checkEmailQuery);
            stmt.setString(1, user.getEmail());
            rs = stmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Email already exists. Please use a different email.");
                return false;
            }

            String salt = passwordHash.generateSalt();
            String hashedPassword = passwordHash.hashPassword(user.getPassword(), salt);

            String query = "INSERT INTO Users (username, password, salt, email, registration_date) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, salt);
            stmt.setString(4, user.getEmail());
            stmt.setDate(5, Date.valueOf(LocalDate.now()));

            return stmt.executeUpdate() > 0;

            // catching exceptions when user tries to insert a duplicate or trying to insert a
            // foreign key value that doesn't exist or violating a check constraint on a column
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("A user with this email or username already exists.");
            return false;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Updates the user's information in the database.
     *
     * @param user the User object containing the updated information (username, email, and user ID).
     * @return true if the update was successful, false otherwise.
     * @throws SQLException if a database access error occurs during the update.
     */

    @Override
    public boolean updateUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            String query = "UPDATE Users SET username = ?, email = ? WHERE user_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getUserId());

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Updates the user's password.
     * Checks the current password against the stored hash before updating.
     *
     * @param userId          User ID to update
     * @param currentPassword Current password to verify
     * @param newPassword      New password to store
     * @return true if password updated successfully
     * @throws SQLException             If database error occurs
     * @throws NoSuchAlgorithmException If hashing algorithm not found
     * @throws InvalidKeySpecException  If password hashing fails
     */
    @Override
    public boolean updatePassword(int userId, String currentPassword, String newPassword)
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String selectQuery = "SELECT password, salt FROM Users WHERE user_id = ?";
            selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                String storedSalt = rs.getString("salt");

                String computedHash = passwordHash.hashPassword(currentPassword, storedSalt);

                if (!computedHash.equals(storedHash)) {
                    return false;
                }

                String newSalt = passwordHash.generateSalt();
                String newHash = passwordHash.hashPassword(newPassword, newSalt);

                String updateQuery = "UPDATE Users SET password = ?, salt = ? WHERE user_id = ?";
                updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newHash);
                updateStmt.setString(2, newSalt);
                updateStmt.setInt(3, userId);

                return updateStmt.executeUpdate() > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (selectStmt != null) selectStmt.close();
            if (updateStmt != null) updateStmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Renews the subscription for a given user by extending the subscription end date by one year.
     *
     * @param userId      the ID of the user whose subscription is to be renewed.
     * @param currentDate the current date used to determine the new subscription end date.
     * @return true if the subscription was successfully renewed, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean renewSubscription(int userId, LocalDate currentDate) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String selectQuery = "SELECT subscription_end_date FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(selectQuery);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate currentEndDate = rs.getDate("subscription_end_date").toLocalDate();
                LocalDate newEndDate;

                if (currentDate.isAfter(currentEndDate)) {
                    newEndDate = currentDate.plusYears(1);
                } else {
                    newEndDate = currentEndDate.plusYears(1);
                }

                String updateQuery = "UPDATE Users SET subscription_end_date = ? WHERE user_id = ?";
                stmt = conn.prepareStatement(updateQuery);
                stmt.setDate(1, Date.valueOf(newEndDate));
                stmt.setInt(2, userId);

                return stmt.executeUpdate() > 0;
            }
            return false;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve
     * @return User object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    @Override
    public User getUserById(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            String query = "SELECT * FROM Users WHERE user_id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("salt"),
                        rs.getString("email"),
                        rs.getDate("registration_date").toLocalDate(),
                        rs.getDate("subscription_start_date").toLocalDate(),
                        rs.getDate("subscription_end_date").toLocalDate()
                );
            }
        } catch (SQLException e) {
            System.out.println(LocalDate.now() + ": Error getting user by ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        return user;
    }

}