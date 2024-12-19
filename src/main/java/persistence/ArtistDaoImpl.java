package persistence;

import business.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArtistDaoImpl extends MySQLDao implements ArtistDao {
    public ArtistDaoImpl() {
        super();
    }

    public ArtistDaoImpl(String dbName) {
        super(dbName);
    }

    /**
     * Retrieves all artists from the database.
     *
     * @return a list of Artist objects
     * @throws SQLException if a database access error occurs
     * @auther Seb Mathews-Lynch
     */
    @Override
    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();

        Connection conn = super.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM artists")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Artist artist = new Artist(
                            rs.getInt("artist_id"),
                            rs.getString("artist_first_name"),
                            rs.getString("artist_last_name"),
                            rs.getBoolean("band"),
                            rs.getString("description")
                    );
                    artists.add(artist);
                }
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": An SQLException occurred while running the query " +
                        "or processing the result.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": An SQLException occurred while preparing the SQL statement.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return artists;
    }
    public static void main(String[] args) {
        //for testing
        try {
            ArtistDao ad = new ArtistDaoImpl("database.properties");

            List<Artist> artistList = ad.getAllArtists();

            if (artistList.isEmpty()) {
                System.out.println("No artists found in the database.");
            } else {
                System.out.println("Artists in the library:");
                for (Artist artist : artistList) {
                    System.out.println(artist);
                }
            }

        } catch (SQLException e) {
            System.out.println("An error occurred while retrieving artists: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
