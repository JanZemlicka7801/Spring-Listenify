package listenify.persistence;

import listenify.business.Albums;

import java.util.List;

public interface AlbumsDao {
    List<Albums> getAlbumsByArtistName(String artistFirstName, String artistLastName);
    int getAlbumIdByAlbumTitle(String albumTitle);
    List<Albums> getAllAlbums();
}
