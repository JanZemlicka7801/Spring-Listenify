package listenify.persistence;

import listenify.business.Playlist;
import listenify.business.Song;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlaylistDaoImplTest {

    private PlaylistDaoImpl playlistDao;

    @BeforeEach
    void setUp() {
        // Initialize the PlaylistDaoImpl with a test database
        playlistDao = new PlaylistDaoImpl("database_test.properties");
    }

    @Test
    void testCreatePlaylist() throws SQLException {
        // Test creating a playlist
        assertTrue(playlistDao.createPlaylist(1, "My Playlist", true));
        // You can add more assertions to verify the playlist was added correctly
    }

    @Test
    void testRenamePlaylist() throws SQLException {
        // Test renaming a playlist
        assertTrue(playlistDao.renamePlaylist(1, "New Playlist Name"));
        // You can add more assertions to verify the playlist was renamed correctly
    }

    @Test
    void testGetUserPlaylists() throws SQLException {
        // Test retrieving playlists for a user
        List<Playlist> playlists = playlistDao.getUserPlaylists(1);
        assertNotNull(playlists);
        assertFalse(playlists.isEmpty());
    }
    @Test
    void testGetPlaylistSongs() throws SQLException {
        List<Song> songs = playlistDao.getPlaylistSongs(1);
        assertNotNull(songs);
        assertTrue(songs.isEmpty());
    }

    @Test
    void testGetPublicPlaylists() throws SQLException {
        // Test retrieving public playlists
        List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();
        assertNotNull(publicPlaylists);
        assertFalse(publicPlaylists.isEmpty());
    }

    @Test
    void testGetPlaylistByName() throws SQLException {
        // Test finding a playlist by name
        Playlist playlist = playlistDao.getPlaylistByName("Test Playlist");
        assertNotNull(playlist);
    }

    @Test
    void testAddSongToPlaylist() throws SQLException {
        // Test adding a song to a playlist
        assertTrue(playlistDao.addSongToPlaylist(1, 1));
    }

    @Test
    void testRemoveSongFromPlaylist() throws SQLException {
        // Test removing a song from a playlist
        assertTrue(playlistDao.removeSongFromPlaylist(1, 1));
    }

    @Test
    void testIsPlaylistOwner() throws SQLException {
        // Test checking if a user owns a playlist
        assertTrue(playlistDao.isPlaylistOwner(1, 1));
    }

    @Test
    void testCanUserAccessPlaylist() throws SQLException {
        // Test checking if a user can access a playlist
        assertTrue(playlistDao.canUserAccessPlaylist(1, 1));
    }

    @Test
    void testGetPlaylistById() throws SQLException {
        // Test retrieving a playlist by its ID
        Playlist playlist = playlistDao.getPlaylistById(1);
        assertNotNull(playlist);
    }

    @AfterEach
    void tearDown() {
        // Cleanup resources if necessary
    }
}
