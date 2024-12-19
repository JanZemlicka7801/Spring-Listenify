package persistence;

import business.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
//done by Omer
public interface UserDao {
    public User login(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException;
    boolean register(User user) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException;
    List<User> getAllUsers() throws SQLException;
}
