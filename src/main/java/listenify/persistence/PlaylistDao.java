package listenify.persistence;

import listenify.business.Playlist;
import listenify.business.Song;

import java.sql.SQLException;
import java.util.List;

public interface PlaylistDao {
    //done by Omer
    boolean createPlaylist(int userId, String name, boolean isPublic) throws SQLException;
    boolean renamePlaylist(int playlistId, String newName) throws SQLException;
    List<Playlist> getUserPlaylists(int userId) throws SQLException;
    List<Playlist> getPublicPlaylists() throws SQLException;
    Playlist getPlaylistByName(String name) throws SQLException;
    boolean addSongToPlaylist(int playlistId, int songId) throws SQLException;
    boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
    List<Song> getPlaylistSongs(int playlistId) throws SQLException;

    boolean isPlaylistOwner(int playlistId, int userId) throws SQLException;
    boolean canUserAccessPlaylist(int playlistId, int userId) throws SQLException;

    Playlist getPlaylistById(int playlistId) throws SQLException;
}