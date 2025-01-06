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

/**
 * Controller for managing artist-related operations in the Listenify application.
 */
@Controller
public class ArtistController {

    private final ArtistDao artistDao;
    private final SongDao songDao;

    /**
     * Initializes the ArtistController with required DAO implementations.
     */
    public ArtistController() {
        this.artistDao = new ArtistDaoImpl();
        this.songDao = new SongDaoImpl("database.properties");
    }

    /**
     * Handles the request to view all artists.
     *
     * @param model   the Model object used to pass data to the view.
     * @param session the HttpSession to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying artists or redirects to the login page if the user is not logged in.
     */
    @GetMapping("/viewArtists")
    public String processRequest(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Artist> artists = artistDao.getAllArtists();
        if (artists.isEmpty()) {
            model.addAttribute("errMsg", "No artists found.");
        } else {
            model.addAttribute("artists", artists);
        }
        return "artists";
    }

    /**
     * Handles the request to search for artists by name.
     *
     * @param artistName the name of the artist to search for.
     * @param model      the Model object used to pass data to the view.
     * @param session    the HttpSession to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying the search results or redirects to the login page if the user is not logged in.
     */
    @GetMapping("/searchArtists")
    public String searchArtists(@RequestParam(name = "artistName") String artistName, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Artist> artists = artistDao.searchArtistsByName(artistName);
        if (artists.isEmpty()) {
            model.addAttribute("errMsg", "No artists found for the given name.");
        }
        model.addAttribute("artists", artists);
        model.addAttribute("searchQuery", artistName);
        return "artists";
    }

    /**
     * Handles the request to search for songs by a specific artist.
     *
     * @param artistFirstName the first name of the artist (optional).
     * @param artistLastName  the last name of the artist (required).
     * @param model           the Model object used to pass data to the view.
     * @param session         the HttpSession to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying the artist's songs or redirects to the login page if the user is not logged in.
     */
    @GetMapping("/searchArtistSongs")
    public String searchArtistSongs(
            @RequestParam(name = "firstName", required = false) String artistFirstName,
            @RequestParam(name = "lastName") String artistLastName,
            Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Song> songs = songDao.searchSongsByArtist(artistFirstName, artistLastName);

        if (songs.isEmpty()) {
            model.addAttribute("errMsg", "No songs found for the given artist.");
        } else {
            model.addAttribute("songs", songs);
            model.addAttribute("artistName", artistFirstName != null ? artistFirstName + " " + artistLastName : artistLastName);
        }

        return "artistList";
    }
}