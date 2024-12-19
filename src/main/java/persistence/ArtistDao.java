package persistence;

import business.Artist;

import java.sql.SQLException;
import java.util.List;

public interface ArtistDao {
    List<Artist> getAllArtists() throws SQLException;
}
