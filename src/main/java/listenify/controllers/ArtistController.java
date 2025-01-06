package listenify.controllers;

import listenify.business.Artist;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import listenify.persistence.ArtistDao;
import listenify.persistence.ArtistDaoImpl;

import java.util.List;

@Controller
public class ArtistController {

    private final ArtistDao artistDao;

    public ArtistController(){
        this.artistDao = new ArtistDaoImpl();
    }

    @GetMapping("/viewArtists")
    public String processRequest(Model model) {
        List<Artist> artists = artistDao.getAllArtists();
        model.addAttribute("artists", artists);
        return "artists";
    }

}