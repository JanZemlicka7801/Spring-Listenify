package persistence;

import business.User;
import functions.PasswordHash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
//done by Omer
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
                String computedHash = PasswordHash.hashPassword(password, storedSalt);

                if (computedHash.equals(storedHash)) {
                    user = new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            storedHash,
                            storedSalt,
                            rs.getString("email"),
                            rs.getDate("registration_date").toLocalDate()
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

            String salt = PasswordHash.generateSalt();
            String hashedPassword = PasswordHash.hashPassword(user.getPassword(), salt);

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
}