package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingDaoImpl extends MySQLDao implements RatingDao {

    public RatingDaoImpl(String dbName) {super(dbName);}

    /**
     * Rates a song by a user, or updates the rating if it already exists.
     *
     * @param userId the ID of the user
     * @param songId the ID of the song
     * @param rating the rating given by the user, must be between 1 and 5
     * @return true if the rating was successfully inserted/updated, false otherwise
     * @throws SQLException if a database access error occurs
     * @auther Jan Zemlicka
     */
    @Override
    public boolean rateSong(int userId, int songId, int rating) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        if (userId <= 0 || songId <= 0) {
            throw new IllegalArgumentException("User ID and Song ID must be positive integers.");
        }

        String query = "INSERT INTO Ratings (user_id, song_id, rating) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE rating = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setInt(2, songId);
            ps.setInt(3, rating);
            ps.setInt(4, rating);
            ps.executeUpdate();
            return true;
        }
    }

    /**
     * Retrieves a list of songs rated by a specified user.
     *
     * @param userId the ID of the user
     * @return a list of strings containing song titles and ratings, or an empty list if no ratings are found
     * @throws SQLException if a database access error occurs
     * @auther Jan Zemlicka
     */
    @Override
    public List<String> viewRatedSongs(int userId) throws SQLException {
        List<String> ratedSongs = new ArrayList<>();
        String query = "SELECT s.song_title, r.rating FROM Songs s "
                + "JOIN Ratings r ON s.song_id = r.song_id "
                + "WHERE r.user_id = ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String songInfo = "Song: " + rs.getString("song_title") + ", Rating: " + rs.getInt("rating");
                ratedSongs.add(songInfo);
            }
        } catch (SQLException e) {
            System.err.println("SQLException in viewRatedSongs: " + e.getMessage());
            e.printStackTrace();
        }
        return ratedSongs;
    }

    /**
     * Retrieves the top-rated song based on average rating.
     *
     * @return a string with the top-rated song and its average rating, or a message if no ratings exist
     * @throws SQLException if a database access error occurs
     * @auther Jan Zemlicka
     */
    @Override
    public String getTopRatedSong() throws SQLException {
        String query = "SELECT s.song_title, AVG(r.rating) as avg_rating FROM Songs s "
                + "JOIN Ratings r ON s.song_id = r.song_id "
                + "GROUP BY s.song_id "
                + "ORDER BY avg_rating DESC LIMIT 1";

        try (Connection conn = super.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to establish a database connection.");
                return "No ratings found.";
            }

            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return "Top-Rated Song: " + rs.getString("song_title")
                            + ", Average Rating: " + rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getTopRatedSong: " + e.getMessage());
            e.printStackTrace();
        }
        return "No ratings found.";
    }

    /**
     * Retrieves the most popular song based on the number of playlists it appears in.
     *
     * @return a string with the most popular song and the count of playlists, or a message if no data exists
     * @throws SQLException if a database access error occurs
     * @auther Jan Zemlicka
     */
    @Override
    public String getTheMostPopularSong() throws SQLException {
        String query = "SELECT s.song_title, COUNT(ps.playlist_id) as playlist_count FROM Songs s "
                + "JOIN Playlist_Songs ps ON s.song_id = ps.song_id "
                + "GROUP BY s.song_id "
                + "ORDER BY playlist_count DESC LIMIT 1";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return "Most Popular Song: " + rs.getString("song_title")
                        + ", In Playlists: " + rs.getInt("playlist_count");
            }
        } catch (SQLException e) {
            System.err.println("SQLException in getTheMostPopularSong: " + e.getMessage());
            e.printStackTrace();
        }
        return "No most popular song.";
    }
}
