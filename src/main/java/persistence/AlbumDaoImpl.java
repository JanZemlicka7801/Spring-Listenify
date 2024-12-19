package persistence;

import business.Albums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlbumDaoImpl extends MySQLDao implements AlbumsDao{

    public AlbumDaoImpl(){
        super();
    }

    public AlbumDaoImpl(String dbName){
        super(dbName);
    }

    /**
     * Retrieves a list of albums by the given artist's first and last names.
     *
     * @param artistFirstName The first name of the artist (can be null or empty).
     * @param artistLastName The last name of the artist (must not be null).
     * @return A list of albums associated with the specified artist.
     * @author Seb Mathews-Lynch
     */
    @Override
    public List<Albums> getAlbumsByArtistName(String artistFirstName, String artistLastName) {
        List<Albums> albumsList = new ArrayList<>();
        String query;

        if (artistFirstName == null || artistFirstName.isEmpty()) {
            // Query for artists with only a last name
            query = "SELECT a.album_id, a.artist_id, a.album_title, a.release_year " +
                    "FROM albums a " +
                    "JOIN artists ar ON a.artist_id = ar.artist_id " +
                    "WHERE ar.artist_first_name IS NULL AND ar.artist_last_name = ?";
        } else {
            // Query for artists with both first and last names
            query = "SELECT a.album_id, a.artist_id, a.album_title, a.release_year " +
                    "FROM albums a " +
                    "JOIN artists ar ON a.artist_id = ar.artist_id " +
                    "WHERE ar.artist_first_name = ? AND ar.artist_last_name = ?";
        }

        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            if (artistFirstName == null || artistFirstName.isEmpty()) {
                // Set only the last name parameter
                ps.setString(1, artistLastName);
            } else {
                // Set both first and last name parameters
                ps.setString(1, artistFirstName);
                ps.setString(2, artistLastName);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Albums album = new Albums();
                    album.setAlbum_id(rs.getInt("album_id"));
                    album.setArtist_id(rs.getInt("artist_id"));
                    album.setAlbum_title(rs.getString("album_title"));
                    album.setRelease_year(rs.getInt("release_year"));
                    albumsList.add(album);
                }
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": SQLException occurred while executing the query or processing the result set.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": SQLException occurred while preparing the SQL statement.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return albumsList;
    }

    /**
     * Retrieves the album ID based on the album title. Used for SongDao
     *
     * @param albumTitle The title of the album to search for.
     * @return The album ID if found; -1 if no matching album exists.
     * @auther Seb Mathews-Lynch
     */
    public int getAlbumIdByAlbumTitle(String albumTitle) {
        int albumId = -1;
        String query = "SELECT album_id FROM albums WHERE album_title = ?";
        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, albumTitle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    albumId = rs.getInt("album_id");
                }
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": SQLException occurred while processing results.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": SQLException occurred while preparing the SQL statement.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return albumId;
    }

    public static void main(String[] args) {
        //for testing
        AlbumsDao ab = new AlbumDaoImpl("database.properties");
        String n = null;
        System.out.println(ab.getAlbumsByArtistName(n,"OneRepublic"));
    }
}