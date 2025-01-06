package listenify.controllers;

import listenify.persistence.RatingDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class IndexController {

    private final RatingDaoImpl ratingDao;

    public IndexController(){
        this.ratingDao = new RatingDaoImpl("database.properties");
    }

    @GetMapping("/")
    public String homePage(Model model) {
        String mostPopularSong;
        try {
            mostPopularSong = ratingDao.getTheMostPopularSong();
        } catch (Exception e) {
            mostPopularSong = "Unable to fetch the most popular song.";
            e.printStackTrace();
        }
        model.addAttribute("mostPopularSong", mostPopularSong);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}