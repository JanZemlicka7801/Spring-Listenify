package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Playlist;
import listenify.business.Song;
import listenify.business.User;
import listenify.persistence.PlaylistDao;
import listenify.persistence.PlaylistDaoImpl;
import listenify.persistence.SongDao;
import listenify.persistence.SongDaoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing song-related operations in the Listenify application.
 */
@Slf4j
@Controller
public class SongController {

    /**
     * Displays all songs or filters songs by title if a search query is provided.
     *
     * @param title   the title of the song to search for (optional).
     * @param model   the {@link Model} object used to pass data to the view.
     * @param session the {@link HttpSession} to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying songs or redirects to the login page if the user is not logged in.
     */
    @GetMapping("/viewSongs")
    public String viewSongs(@RequestParam(name = "title", required = false) String title, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        SongDao songDao = new SongDaoImpl("database.properties");
        List<Song> songs;
        List<Playlist> userPlaylists = new ArrayList<>();

        try {
            userPlaylists = playlistDao.getUserPlaylists(loggedInUser.getUserId());

            if (title != null && !title.isBlank()) {
                songs = songDao.searchSongsByTitle(title);
                model.addAttribute("searchQuery", title);
            } else {
                songs = songDao.getAllSongs();
            }

            model.addAttribute("songs", songs);
            model.addAttribute("userPlaylists", userPlaylists);
            return "songs";

        } catch (SQLException e) {
            log.error("Error fetching songs or playlists: {}", e.getMessage());
            model.addAttribute("errMsg", "Error fetching data");
            return "error";
        }
    }

    /**
     * Fetches details for a specific song by title.
     *
     * @param title   the title of the song to fetch.
     * @param model   the {@link Model} object used to pass data to the view.
     * @param session the {@link HttpSession} to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying the song details, or an error template if the song is not found or the user is not logged in.
     */
    @GetMapping("/getSong")
    public String getSong(@RequestParam(name = "title") String title, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (title == null || title.isBlank()) {
            model.addAttribute("errMsg", "Valid song title must be provided.");
            return "error";
        }

        try {
            SongDao songDao = new SongDaoImpl("database.properties");
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

            Song song = songDao.getSongByTitle(title);
            List<Playlist> userPlaylists = playlistDao.getUserPlaylists(loggedInUser.getUserId());

            if (song != null) {
                model.addAttribute("song", song);
                model.addAttribute("userPlaylists", userPlaylists);
                return "songList";
            } else {
                model.addAttribute("errMsg", "No song found with title: " + title);
                return "error";
            }
        } catch (SQLException e) {
            log.error("Error fetching song details: {}", e.getMessage());
            model.addAttribute("errMsg", "Error fetching song details");
            return "error";
        }
    }

    /**
     * Searches for songs by title.
     *
     * @param songName the title of the song to search for.
     * @param model    the {@link Model} object used to pass data to the view.
     * @param session  the {@link HttpSession} to check for logged-in user information.
     * @return the name of the Thymeleaf template for displaying the search results, or an error template if an exception occurs.
     */
    @GetMapping("/searchSongs")
    public String searchSongs(
            @RequestParam(name = "songName") String songName,
            Model model,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            SongDao songDao = new SongDaoImpl("database.properties");
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

            List<Song> songs = songDao.searchSongsByTitle(songName);
            List<Playlist> userPlaylists = playlistDao.getUserPlaylists(loggedInUser.getUserId());

            model.addAttribute("songs", songs);
            model.addAttribute("userPlaylists", userPlaylists);
            return "songList";

        } catch (SQLException e) {
            log.error("Error searching songs: {}", e.getMessage());
            model.addAttribute("errMsg", "Error searching songs");
            return "error";
        }
    }
}