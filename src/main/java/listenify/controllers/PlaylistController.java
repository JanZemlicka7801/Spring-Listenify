package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Playlist;
import listenify.business.Song;
import listenify.business.User;
import listenify.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/playlists")
public class PlaylistController {

    @GetMapping
    public String listPlaylists(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
            List<Playlist> userPlaylists = playlistDao.getUserPlaylists(loggedInUser.getUserId());
            List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();

            model.addAttribute("userPlaylists", userPlaylists);
            model.addAttribute("publicPlaylists", publicPlaylists);

            return "playlists";

        } catch (SQLException e) {
            log.error("Error getting playlists: {}", e.getMessage());
            model.addAttribute("errMsg", "Failed to retrieve playlists");
            return "error";
        }
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "createPlaylist";
    }

    @PostMapping("/create")
    public String createPlaylist(
            @RequestParam("playlistName") String playlistName,
            @RequestParam(value = "isPublic", defaultValue = "false") boolean isPublic,
            Model model,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (playlistName == null || playlistName.trim().isEmpty()) {
            model.addAttribute("errMsg", "Playlist name cannot be empty");
            return "error";
        }

        try {
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

            if (playlistDao.createPlaylist(loggedInUser.getUserId(), playlistName, isPublic)) {
                return "redirect:/playlists";
            } else {
                model.addAttribute("errMsg", "Failed to create playlist");
                return "error";
            }

        } catch (SQLException e) {
            log.error("Error creating playlist: {}", e.getMessage());
            model.addAttribute("errMsg", "Error creating playlist");
            return "error";
        }
    }

    @GetMapping("/{playlistId}")
    public String viewPlaylist(
            @PathVariable int playlistId,
            Model model,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
            UserDao userDao = new UserDaoImpl("database.properties");
            SongDao songDao = new SongDaoImpl("database.properties");

            Playlist playlist = playlistDao.getPlaylistById(playlistId);
            if (playlist == null) {
                model.addAttribute("errMsg", "Playlist not found");
                return "error";
            }

            boolean canAccess = playlistDao.canUserAccessPlaylist(playlistId, loggedInUser.getUserId());
            if (!canAccess) {
                model.addAttribute("errMsg", "You don't have permission to view this playlist");
                return "error";
            }

            boolean isOwner = playlist.getUser_id() == loggedInUser.getUserId();
            User owner = userDao.getUserById(playlist.getUser_id());
            List<Song> playlistSongs = playlistDao.getPlaylistSongs(playlistId);
            List<Song> availableSongs = songDao.getAllSongs();

            model.addAttribute("playlist", playlist);
            model.addAttribute("playlistOwner", owner);
            model.addAttribute("playlistSongs", playlistSongs);
            model.addAttribute("isOwner", isOwner);
            if (isOwner) {
                model.addAttribute("availableSongs", availableSongs);
            }

            return "playlistView";

        } catch (SQLException e) {
            log.error("Error viewing playlist {}: {}", playlistId, e.getMessage());
            model.addAttribute("errMsg", "Error accessing playlist");
            return "error";
        }
    }

    @PostMapping("/{playlistId}/addSong")
    public String addSongToPlaylist(
            @PathVariable int playlistId,
            @RequestParam int songId,
            Model model,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

            if (!playlistDao.isPlaylistOwner(playlistId, loggedInUser.getUserId())) {
                model.addAttribute("errMsg", "You can only modify your own playlists");
                return "error";
            }

            if (playlistDao.addSongToPlaylist(playlistId, songId)) {
                return "redirect:/playlists/" + playlistId;
            } else {
                model.addAttribute("errMsg", "Failed to add song to playlist");
                return "error";
            }

        } catch (SQLException e) {
            log.error("Error adding song to playlist {}: {}", playlistId, e.getMessage());
            model.addAttribute("errMsg", "Error modifying playlist");
            return "error";
        }
    }

    @PostMapping("/{playlistId}/removeSong")
    public String removeSongFromPlaylist(
            @PathVariable int playlistId,
            @RequestParam int songId,
            Model model,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

            if (!playlistDao.isPlaylistOwner(playlistId, loggedInUser.getUserId())) {
                model.addAttribute("errMsg", "You can only modify your own playlists");
                return "error";
            }

            if (playlistDao.removeSongFromPlaylist(playlistId, songId)) {
                return "redirect:/playlists/" + playlistId;
            } else {
                model.addAttribute("errMsg", "Failed to remove song from playlist");
                return "error";
            }

        } catch (SQLException e) {
            log.error("Error removing song from playlist {}: {}", playlistId, e.getMessage());
            model.addAttribute("errMsg", "Error modifying playlist");
            return "error";
        }
    }
}