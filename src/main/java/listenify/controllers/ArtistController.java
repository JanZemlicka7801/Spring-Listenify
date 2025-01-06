package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Artist;
import listenify.business.Song;
import listenify.business.User;
import listenify.persistence.SongDao;
import listenify.persistence.SongDaoImpl;
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
    private final SongDao songDao;

    public ArtistController(){
        this.artistDao = new ArtistDaoImpl();
        this.songDao = new SongDaoImpl();
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
    public String searchArtists(@RequestParam(name = "artistName") String artistName, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Artist> artists = artistDao.searchArtistsByName(artistName);
        model.addAttribute("artists", artists);
        model.addAttribute("searchQuery", artistName);
        return "artists";
    }

    @GetMapping("/searchArtistSongs")
    public String searchArtistSongs(
            @RequestParam(name = "firstName", required = false) String artistFirstName,
            @RequestParam(name = "lastName") String artistLastName,
            Model model) {

        List<Song> songs = songDao.searchSongsByArtist(artistFirstName, artistLastName);

        if (songs.isEmpty()) {
            model.addAttribute("errMsg", "No songs found for the given artist.");
        } else {
            model.addAttribute("songs", songs);
        }

        return "artistList";
    }
}