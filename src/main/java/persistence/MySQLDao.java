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
    private Connection conn;

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

    public MySQLDao(Connection conn){
        this.conn = conn;
    }

    /**
     * Creates connection with database using the database.properties file as param
     *
     * @return A Connection object if successful, null otherwise.
     */
    public Connection getConnection() {

        try{
            if (properties == null) {
                throw new IllegalStateException("Database properties are not initialized. Ensure the DAO is properly constructed.");
            }

            if (conn == null || conn.isClosed()) {
                // Retrieve database connection properties
                String driver = properties.getProperty("driver");
                String url = properties.getProperty("url");
                String database = properties.getProperty("database", databaseName);
                String username = properties.getProperty("username");
                String password = properties.getProperty("password", "");

                Class.forName(driver);

                conn = DriverManager.getConnection(url + database, username, password);
            }
        } catch (Exception e) {
            System.err.println("Error getting connection: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    public void freeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(LocalDateTime.now() + ": Failed to close database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}