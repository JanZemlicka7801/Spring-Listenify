package listenify.persistence;

import listenify.business.Song;

import java.util.List;

public interface SongDao {
    List<Song> getAllSongsByAlbumId(int albumId);
    Song getSongByTitle(String title);
    List<Song> searchSongsByArtist(String artistFirstName, String artistLastName);
    List<Song> getAllSongs();
    List<Song> searchSongsByTitle(String keyword);

    List<Song> getSongsByArtistName(String artistName);

    List<Song> getSongsByAlbumTitle(String albumTitle);
}
