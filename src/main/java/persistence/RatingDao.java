package persistence;

import java.sql.SQLException;
import java.util.List;

public interface RatingDao {
    boolean rateSong(int userId, int songId, int rating) throws SQLException;
    List<String> viewRatedSongs(int userId) throws SQLException;
    String getTopRatedSong() throws SQLException;
    String getTheMostPopularSong() throws SQLException;
}
