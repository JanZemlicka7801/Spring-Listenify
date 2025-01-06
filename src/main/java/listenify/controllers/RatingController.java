package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.User;
import listenify.persistence.RatingDaoImpl;
import listenify.persistence.UserDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;
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
}
