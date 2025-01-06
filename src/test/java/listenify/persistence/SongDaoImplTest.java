package listenify.persistence;

import listenify.business.Song;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SongDaoImplTest {
    private MySQLDao connectionSource = new MySQLDao("database_test.properties");

    @Test
    void getAllSongsByAlbumId_ReturnsSongs() throws SQLException {
        Connection conn = connectionSource.getConnection();
        conn.setAutoCommit(false);
        SongDao songDao = new SongDaoImpl("database_test.properties");

        List<Song> songs = songDao.getAllSongsByAlbumId(1); // Use a valid album ID from your test database
        assertNotNull(songs, "The list of songs should not be null.");
        assertFalse(songs.isEmpty(), "The list of songs should not be empty.");
        conn.rollback();
    }

    @Test
    void getSongByTitle_Found() throws SQLException {
        Connection conn = connectionSource.getConnection();
        conn.setAutoCommit(false);
        SongDao songDao = new SongDaoImpl("database_test.properties");

        Song song = songDao.getSongByTitle("Love"); // Use a valid title from your test database
        assertNotNull(song, "The song should not be null.");
        assertEquals("Love Of My Life", song.getSongTitle(), "The song title should match the search term.");
        conn.rollback();
    }

    @Test
    void getSongByTitle_NotFound() throws SQLException {
        Connection conn = connectionSource.getConnection();
        conn.setAutoCommit(false);
        SongDao songDao = new SongDaoImpl("database_test.properties");

        Song song = songDao.getSongByTitle("NonExistentTitle");
        assertNull(song, "The song should be null for a non-existent title.");
        conn.rollback();
    }

    @Test
    void searchSongsByTitle_ReturnsSongs() throws SQLException {
        Connection conn = connectionSource.getConnection();
        conn.setAutoCommit(false);
        SongDao songDao = new SongDaoImpl("database_test.properties");

        List<Song> songs = songDao.searchSongsByTitle("Love"); // Use a keyword that matches songs in your test database
        assertNotNull(songs, "The list of songs should not be null.");
        assertFalse(songs.isEmpty(), "The list of songs should not be empty.");
        conn.rollback();
    }

    @Test
    void getAllSongs_ReturnsSongs() throws SQLException {
        Connection conn = connectionSource.getConnection();
        conn.setAutoCommit(false);
        SongDao songDao = new SongDaoImpl("database_test.properties");

        List<Song> songs = songDao.getAllSongs();
        assertNotNull(songs, "The list of songs should not be null.");
        assertFalse(songs.isEmpty(), "The list of songs should not be empty.");
        conn.rollback();
    }
}
