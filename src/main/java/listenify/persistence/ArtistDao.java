package listenify.persistence;

import listenify.business.Artist;

import java.util.List;

public interface ArtistDao {
    List<Artist> getAllArtists();
}
