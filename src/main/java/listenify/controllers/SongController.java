package listenify.controllers;


import listenify.business.Song;
import listenify.persistence.SongDao;
import listenify.persistence.SongDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
public class SongController {

    @GetMapping("/viewSongs")
    public String viewSongs(Model model) {
        SongDao songDao = new SongDaoImpl("database.properties");
        List<Song> songs = songDao.getAllSongsByAlbumId(1);
        model.addAttribute("songs", songs);
        return "songList";
    }

    @GetMapping("/getSong")
    public String getSong(@RequestParam(name = "title") String title, Model model) {
        if (title == null || title.isBlank()) {
            String errorMessage = "Valid song title must be provided.";
            model.addAttribute("errMsg", errorMessage);
            return "error";
        }

        SongDao songDao = new SongDaoImpl("database.properties");
        Song song = songDao.getSongByTitle(title);

        if (song != null) {
            model.addAttribute("song", song);
            return "songList";
        } else {
            model.addAttribute("errMsg", "No song found with title: " + title);
            return "error";
        }
    }

    @GetMapping("/searchSong")
    public String searchForm() {
        return "searchSongs";
    }

    @GetMapping("/searchSongs")
    public String searchSongs(@RequestParam(name = "songName") String songName, Model model) {
        SongDao songDao = new SongDaoImpl("database.properties");
        List<Song> songs = songDao.searchSongsByTitle(songName);
        model.addAttribute("songs", songs);
        return "songList";
    }
}
