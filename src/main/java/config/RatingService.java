package config;

import business.Song;
import business.User;
import persistence.RatingDao;
import persistence.SongDao;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class RatingService extends MainMenu{
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a new RatingService instance with the specified user and scanner.
     *
     * @param user    The user for whom the rating service is created.
     * @param scanner The scanner used to receive input from the user.
     * @auther Jan Zemlicka
     */
    public RatingService(User user, Scanner scanner) {
        super(user, scanner);
    }

    /**
     * Allows a user to rate a song by entering the song name and a rating between 1 and 5.
     * If the song name is not found, the user is informed. If the rating is out of range, an error message is shown.
     *
     * @param userId    The ID of the user who is rating the song.
     * @param ratingDao The data access object for managing song ratings.
     * @param songDao   The data access object for retrieving song information.
     * @auther Jan Zemlicka
     */
    public static void rateSong(int userId, RatingDao ratingDao, SongDao songDao) {
        System.out.print("Enter song name: ");
        String songName = scanner.nextLine().trim();

        Song song;
        song = songDao.getSongByTitle(songName);
        if (song.getSongId() == -1) {
            System.out.println("Song not found. Please enter a valid song name.");
            return;
        }

        System.out.print("Enter rating (1-5): ");
        int rating = Integer.parseInt(scanner.nextLine().trim());

        try {
            if (rating < 1 || rating > 5) {
                System.out.println("Rating must be between 1 and 5.");
                return;
            }
            if (ratingDao.rateSong(userId, song.getSongId(), rating)) {
                System.out.println("Rating submitted successfully.");
            } else {
                System.out.println("Failed to submit rating.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while rating the song.");
        }
    }

    /**
     * Displays a list of songs rated by the user. If the user has not rated any songs,
     * a message is shown to indicate this.
     *
     * @param userId    The ID of the user whose rated songs are being displayed.
     * @param ratingDao The data access object for managing song ratings.
     * @auther Jan Zemlicka
     */
    public static void viewRatedSongs(int userId, RatingDao ratingDao) {
        try {
            List<String> ratedSongs = ratingDao.viewRatedSongs(userId);
            if (ratedSongs.isEmpty()) {
                System.out.println("You haven't rated any songs.");
            } else {
                System.out.println("Your Rated Songs:");
                for (String song : ratedSongs) {
                    System.out.println(song);
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not fetch rated songs.");
        }
    }

    /**
     * Displays the top-rated song based on the average rating. If no songs have been rated,
     * a message is shown to inform the user.
     *
     * @param ratingDao The data access object for managing song ratings.
     * @auther Jan Zemlicka
     */
    public static void viewTopRatedSong(RatingDao ratingDao) {
        try {
            String topRatedSong = ratingDao.getTopRatedSong();
            System.out.println(topRatedSong != null ? topRatedSong : "No ratings found.");
        } catch (SQLException e) {
            System.out.println("Could not fetch the top-rated song.");
        }
    }

    /**
     * Displays the most popular song, defined as the song appearing in the most playlists.
     * If no song is found in any playlists, a message is displayed to inform the user.
     *
     * @param ratingDao The data access object for managing song ratings.
     * @auther Jan Zemlicka
     */
    public static void viewMostPopularSong(RatingDao ratingDao) {
        try {
            String mostPopularSong = ratingDao.getTheMostPopularSong();
            System.out.println(mostPopularSong != null ? mostPopularSong : "No popular song found.");
        } catch (SQLException e) {
            System.out.println("Could not fetch the most popular song.");
        }
    }
}