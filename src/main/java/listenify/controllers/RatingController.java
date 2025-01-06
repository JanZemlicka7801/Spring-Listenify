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

@Controller
public class RatingController {

    private final RatingDaoImpl ratingDao;

    public RatingController() {
        this.ratingDao = new RatingDaoImpl("database.properties");
    }

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
