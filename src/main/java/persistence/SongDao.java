package persistence;

import business.Song;

import java.util.List;

public interface SongDao {
    List<Song> getAllSongsByAlbumId(int albumId);
    Song getSongByTitle(String title);
    List<Song> searchSongsByArtist(String artistFirstName, String artistLastName);
}
