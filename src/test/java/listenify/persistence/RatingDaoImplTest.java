package listenify.persistence;

import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RatingDaoImplTest {

    private RatingDaoImpl ratingDao;

    @BeforeEach
    void setUp() {
        ratingDao = new RatingDaoImpl("database_test.properties");
    }

    @Test
    void testRateSong() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> ratingDao.rateSong(-1, 1, 5));
        assertThrows(IllegalArgumentException.class, () -> ratingDao.rateSong(1, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> ratingDao.rateSong(1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> ratingDao.rateSong(1, 1, 6));

        // Assuming the database is empty initially
        assertTrue(ratingDao.rateSong(1, 1, 4));
        assertTrue(ratingDao.rateSong(1, 1, 5)); // Update the rating
    }

    @Test
    void testViewRatedSongs() throws SQLException {
        // Assuming the database has ratings
        List<String> ratedSongs = ratingDao.viewRatedSongs(1);
        assertNotNull(ratedSongs);
        assertFalse(ratedSongs.isEmpty());
    }

    @Test
    void testGetTopRatedSong() throws SQLException {
        String topRatedSong = ratingDao.getTopRatedSong();
        assertNotNull(topRatedSong);
    }

    @Test
    void testGetTheMostPopularSong() throws SQLException {
        String mostPopularSong = ratingDao.getTheMostPopularSong();
        assertNotNull(mostPopularSong);
    }

    @AfterEach
    void tearDown() {

    }
}
