package listenify.persistence;

import listenify.business.Albums;
import listenify.business.Song;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SongDaoImpl extends MySQLDao implements SongDao {
    public SongDaoImpl() {
        super();
    }

    public SongDaoImpl(String propFilename) {
        super(propFilename);
    }

    /**
     * Retrieves a list of songs associated with a specified album ID.
     *
     * @param albumId the ID of the album for which to retrieve songs
     * @return a list of Song objects belonging to the specified album, or an empty list if no songs are found
     * @auther Seb Mathews-Lynch
     */
    @Override
    public List<Song> getAllSongsByAlbumId(int albumId) {
        List<Song> songs = new ArrayList<>();

        String query = "SELECT * FROM songs WHERE album_id = ?";

        Connection conn = super.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, albumId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Song song = new Song(
                            rs.getInt("song_id"),
                            rs.getInt("album_id"),
                            rs.getString("song_title"),
                            rs.getTime("duration")
                    );
                    songs.add(song);
                }
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": SQLException occurred while running the query or processing results.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": SQLException occurred while preparing the SQL statement.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return songs;
    }

    /**
     * Retrieves a song by searching for a title or partial title match.
     *
     * @param title the title or partial title of the song to search for
     * @return the Songs object with the matching title, or null if no match is found
     * @auther Seb Mathews-Lynch
     */
    @Override
    public Song getSongByTitle(String title) {
        String query = "SELECT * FROM songs WHERE song_title LIKE ?";
        Song song = null;

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, "%" + title + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    song = new Song(
                            rs.getInt("song_id"),
                            rs.getInt("album_id"),
                            rs.getString("song_title"),
                            rs.getTime("duration")
                    );
                }
            } catch (SQLException e) {
                System.out.println(LocalDateTime.now() + ": SQLException occurred while processing the results.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println(LocalDateTime.now() + ": SQLException occurred while preparing the SQL statement.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return song;
    }

    /**
     * Searches for songs by a specific artist.
     *
     * @param artistFirstName the first name of the artist, can be null if the artist has only one name
     * @param artistLastName the last name of the artist
     * @return a list of songs by the specified artist
     * @auther Seb Mathews-Lynch
     */
    @Override
    public List<Song> searchSongsByArtist(String artistFirstName, String artistLastName) {
        List<Song> songs = new ArrayList<>();
        String query;

        if (artistFirstName == null || artistFirstName.isEmpty()) {
            // Query for artists with only a last name
            query = "SELECT s.song_id, s.album_id, s.song_title, s.duration " +
                    "FROM songs s " +
                    "JOIN albums a ON s.album_id = a.album_id " +
                    "JOIN artists ar ON a.artist_id = ar.artist_id " +
                    "WHERE ar.artist_first_name IS NULL AND ar.artist_last_name = ?";
        } else {
            // Query for artists with both first and last names
            query = "SELECT s.song_id, s.album_id, s.song_title, s.duration " +
                    "FROM songs s " +
                    "JOIN albums a ON s.album_id = a.album_id " +
                    "JOIN artists ar ON a.artist_id = ar.artist_id " +
                    "WHERE ar.artist_first_name = ? AND ar.artist_last_name = ?";
        }

        Connection conn = super.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            if (artistFirstName == null || artistFirstName.isEmpty()) {
                ps.setString(1, artistLastName);
            } else {
                ps.setString(1, artistFirstName);
                ps.setString(2, artistLastName);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Song song = new Song();
                    song.setSongId(rs.getInt("song_id"));
                    song.setAlbumId(rs.getInt("album_id"));
                    song.setSongTitle(rs.getString("song_title"));
                    song.setDuration(rs.getTime("duration"));
                    songs.add(song);
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

        return songs;
    }

    @Override
    public List<Song> searchSongsByTitle(String keyword) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs WHERE song_title LIKE ?";
        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("SQLException occurred when searching songs by title keyword: " + keyword, e);
        }
        return songs;
    }

    @Override
    public List<Song> getSongsByArtistName(String artistName) {
        List<Song> songs = new ArrayList<>();
        Connection conn = super.getConnection();

        if (conn == null) {
            System.err.println("Database connection is null. Cannot fetch songs.");
            return songs;
        }

        String query = "SELECT s.* FROM songs s " +
                "JOIN albums alb ON s.album_id = alb.album_id " +
                "JOIN artists art ON alb.artist_id = art.artist_id " +
                "WHERE CONCAT(COALESCE(art.artist_first_name, ''), ' ', art.artist_last_name) LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + artistName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    songs.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching songs by artist name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }
        return songs;
    }

        /**
         * Retrieves a list of songs in a specific album.
         *
         * @param albumTitle The title of the album to search for.
         * @return A list of songs in the specified album.
         */
    @Override
    public List<Song> getSongsByAlbumTitle(String albumTitle) {
        List<Song> songs = new ArrayList<>();
        Connection conn = super.getConnection();

        if (conn == null) {
            System.err.println("Database connection is null. Cannot fetch songs.");
            return songs;
        }

        String query = "SELECT s.* FROM songs s " +
                "JOIN albums a ON s.album_id = a.album_id " +
                "WHERE a.album_title LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + albumTitle + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                songs.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching songs by album title: " + e.getMessage());
            e.printStackTrace();
        } finally {
            super.freeConnection(conn);
        }
        return songs;
    }

    @Override
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT songs.*, albums.album_title " +
                "FROM songs " +
                "JOIN albums ON songs.album_id = albums.album_id";

        try (Connection conn = super.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Song song = new Song(
                        rs.getInt("song_id"),
                        rs.getInt("album_id"),
                        rs.getString("song_title"),
                        rs.getTime("duration")
                );
                songs.add(song);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving songs: " + e.getMessage());
        }

        return songs;
    }

    private Song mapRow(ResultSet rs) throws SQLException {
        return Song.builder()
                .songId(rs.getInt("song_id"))
                .albumId(rs.getInt("album_id"))
                .songTitle(rs.getString("song_title"))
                .duration(rs.getTime("duration"))
                .build();
    }
    public static void main(String[] args) {
        SongDaoImpl s = new SongDaoImpl("database.properties");
        System.out.println(s.getAllSongsByAlbumId(1));

        List<Song> searchResults = s.searchSongsByTitle("Love");
        searchResults.forEach(System.out::println);
    }
}
