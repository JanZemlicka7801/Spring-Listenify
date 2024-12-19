package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;

public class MySQLDao {
    private Properties properties = new Properties();
    private String databaseName = "listenify";

    public MySQLDao() {
    }

    public MySQLDao(String propFilename) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFilename)) {
            if (input == null) {
                throw new IOException("Property file '" + propFilename + "' not found in the classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Error while trying to load properties from " + propFilename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates connection with database using the database.properties file as param
     *
     * @return A Connection object if successful, null otherwise.
     */
    public Connection getConnection() {
        if (properties.isEmpty()) {
            System.out.println("Properties not loaded. Cannot establish database connection.");
            return null;
        }

        // Retrieve database connection properties
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String database = properties.getProperty("database", databaseName);
        String username = properties.getProperty("username");
        String password = properties.getProperty("password", "");

        try {
            Class.forName(driver);

            try {
                // Create and return a connection to the database
                Connection con = DriverManager.getConnection(url + database, username, password);
                return con;
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": SQLException trying to connect to " + url + database);
                System.out.println(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException while trying to load MySQL driver");
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}