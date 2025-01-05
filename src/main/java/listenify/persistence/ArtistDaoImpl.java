package listenify.persistence;

import listenify.business.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistDaoImpl extends MySQLDao implements ArtistDao {
    public ArtistDaoImpl() {
        super("database.properties");
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

        if (conn == null) {
            System.err.println("Database connection is null. Cannot fetch artists.");
            return artists;
        }

        String query = "SELECT * FROM artists";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                artists.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all artists: " + e.getMessage());
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }
        return artists;
    }

    private Artist mapRow(ResultSet rs) throws SQLException {
        return Artist.builder()
                .artistId(rs.getInt("artist_id"))
                .artistFirstName(rs.getString("artist_first_name"))
                .artistLastName(rs.getString("artist_last_name"))
                .band(rs.getByte("band"))
                .description(rs.getString("description"))
                .build();
    }
}
