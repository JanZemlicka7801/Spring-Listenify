package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.User;
import listenify.persistence.RatingDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for managing song ratings and user-related information in the Listenify application.
 */
@Controller
public class RatingController {

    private final RatingDaoImpl ratingDao;

    /**
     * Initializes the RatingController with a RatingDaoImpl instance.
     */
    public RatingController() {
        this.ratingDao = new RatingDaoImpl("database.properties");
    }

    /**
     * Displays the home page with the most popular and top-rated songs.
     *
     * @param model the Model object used to pass data to the view.
     * @return the name of the Thymeleaf template for the home page.
     */
    @GetMapping("/")
    public String homePage(Model model) {
        String mostPopularSong;
        String topRatedSong;

        try {
            topRatedSong = ratingDao.getTopRatedSong();
            mostPopularSong = ratingDao.getTheMostPopularSong();
        } catch (Exception e) {
            topRatedSong = "Unable to fetch the top rated song.";
            mostPopularSong = "Unable to fetch the most popular song.";
            e.printStackTrace();
        }

        model.addAttribute("mostPopularSong", mostPopularSong);
        model.addAttribute("topRatedSong", topRatedSong);
        return "index";
    }

    /**
     * Displays the user's profile page, including their rated songs.
     *
     * @param model   the Model object used to pass data to the view.
     * @param session the HttpSession object to check for logged-in user information.
     * @return the name of the Thymeleaf template for the profile page or redirects to the login page if the user is not logged in.
     * @throws SQLException if an error occurs while retrieving rated songs.
     */
    @GetMapping("/profile")
    public String profilePage(Model model, HttpSession session) throws SQLException {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<String> ratedSongs = ratingDao.viewRatedSongs(loggedInUser.getUserId());
        model.addAttribute("ratedSongs", ratedSongs);
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    /**
     * Handles user ratings for songs.
     *
     * @param songId  the ID of the song being rated.
     * @param rating  the rating provided by the user (e.g., 1 to 5 stars).
     * @param session the HttpSession object to check for logged-in user information.
     * @param model   the Model object used to pass messages to the view.
     * @return redirects to the song list page after rating or to the login page if the user is not logged in.
     */
    @PostMapping("/rateSong")
    public String rateSong(
            @RequestParam("songId") int songId,
            @RequestParam("rating") int rating,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            boolean success = ratingDao.rateSong(user.getUserId(), songId, rating);
            if (success) {
                model.addAttribute("message", "Your rating has been saved!");
            } else {
                model.addAttribute("message", "Failed to save your rating. Please try again.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/viewSongs";
    }
}