package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.business.Albums;
import listenify.business.Song;
import listenify.persistence.SongDao;
import listenify.persistence.SongDaoImpl;
import listenify.business.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import listenify.persistence.AlbumsDao;
import listenify.persistence.AlbumDaoImpl;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumsDao albumsDao;

    public AlbumController() {
        this.albumsDao = new AlbumDaoImpl("database.properties");
    }

    @GetMapping
    public String viewAlbums(Model model, HttpSession session) throws SQLException {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Albums> albums = albumsDao.getAllAlbums();
        model.addAttribute("albums", albums);
        return "albums";
    }

    @GetMapping("/searchAlbum")
    public String searchAlbum(@RequestParam(name = "albumTitle") String albumTitle, Model model) {
        if (albumTitle == null || albumTitle.isBlank()) {
            model.addAttribute("errMsg", "Album title cannot be empty.");
            return "error";
        }

        SongDao songDao = new SongDaoImpl("database.properties");
        List<Song> songs = songDao.getSongsByAlbumTitle(albumTitle);

        if (songs.isEmpty()) {
            model.addAttribute("errMsg", "No songs found for the album: " + albumTitle);
            return "error";
        }

        model.addAttribute("albumTitle", albumTitle);
        model.addAttribute("songs", songs);
        return "albumList";
    }
}