package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Albums;
import listenify.business.Artist;
import listenify.business.Song;
import listenify.business.User;
import listenify.persistence.AlbumDaoImpl;
import listenify.persistence.ArtistDaoImpl;
import listenify.persistence.SongDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for handling search functionality in the Listenify application.
 */
@Controller
public class SearchController {
    private final SongDaoImpl songDao;
    private final ArtistDaoImpl artistDao;
    private final AlbumDaoImpl albumDao;

    /**
     * Initializes the SearchController with the required DAO implementations.
     */
    public SearchController() {
        this.songDao = new SongDaoImpl("database.properties");
        this.artistDao = new ArtistDaoImpl();
        this.albumDao = new AlbumDaoImpl("database.properties");
    }

    /**
     * Handles search requests across songs, artists, and albums.
     *
     * @param query   the search query provided by the user.
     * @param type    the type of search to perform (e.g., "song", "artist", "album", or "all").
     *                Defaults to "all" if not specified.
     * @param model   the {@link Model} object used to pass data to the view.
     * @param session the {@link HttpSession} to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying search results,
     *         or redirects to the login page if the user is not logged in.
     */
    @GetMapping("/search")
    public String search(@RequestParam String query,
                         @RequestParam(defaultValue = "all") String type,
                         Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Song> songs = new ArrayList<>();
        List<Artist> artists = new ArrayList<>();
        List<Albums> albums = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "song":
                // Search for songs by title
                songs = songDao.searchSongsByTitle(query);
                break;
            case "artist":
                // Search for artists by name
                artists = artistDao.searchArtistsByName(query);
                break;
            case "album":
                // Search for albums by artist name
                String[] queryParts = query.split("\\s+");
                if (queryParts.length > 1) {
                    String firstName = queryParts[0];
                    String lastName = String.join(" ",
                            Arrays.copyOfRange(queryParts, 1, queryParts.length));
                    albums = albumDao.getAlbumsByArtistName(firstName, lastName);
                } else {
                    albums = albumDao.getAlbumsByArtistName(null, query);
                }
                break;
            case "all":
            default:
                // Perform a general search across songs, artists, and albums
                songs = songDao.searchSongsByTitle(query);
                artists = artistDao.searchArtistsByName(query);
                albums = albumDao.getAlbumsByArtistName(null, query);
                songs.addAll(songDao.getSongsByArtistName(query));
                songs.addAll(songDao.getSongsByAlbumTitle(query));
                break;
        }

        // Add search results to the model
        model.addAttribute("songs", songs);
        model.addAttribute("artists", artists);
        model.addAttribute("albums", albums);
        model.addAttribute("searchQuery", query);
        model.addAttribute("searchType", type);

        return "searchResult";
    }
}