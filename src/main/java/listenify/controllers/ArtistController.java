package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Artist;
import listenify.business.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import listenify.persistence.ArtistDao;
import listenify.persistence.ArtistDaoImpl;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ArtistController {

    private final ArtistDao artistDao;

    public ArtistController(){
        this.artistDao = new ArtistDaoImpl();
    }

    @GetMapping("/viewArtists")
    public String processRequest(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Artist> artists = artistDao.getAllArtists();
        model.addAttribute("artists", artists);
        return "artists";
    }

    @GetMapping("/searchArtists")
    public String searchArtists(@RequestParam(name = "artistName") String artistName, Model model) {
        List<Artist> artists = artistDao.searchArtistsByName(artistName);
        model.addAttribute("artists", artists);
        model.addAttribute("searchQuery", artistName);
        return "artists";
    }
}