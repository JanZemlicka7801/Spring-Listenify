package listenify.controllers;

import listenify.business.Albums;
import listenify.business.Song;
import listenify.persistence.SongDao;
import listenify.persistence.SongDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import listenify.persistence.AlbumsDao;
import listenify.persistence.AlbumDaoImpl;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AlbumController {

    private final AlbumsDao albumsDao;

    public AlbumController(){
        this.albumsDao = new AlbumDaoImpl("database.properties");
    }

    @GetMapping("/viewAlbums")
    public String processRequest(Model model) {
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