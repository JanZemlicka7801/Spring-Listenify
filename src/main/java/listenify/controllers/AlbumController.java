package listenify.controllers;

import listenify.business.Albums;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import listenify.persistence.AlbumsDao;
import listenify.persistence.AlbumDaoImpl;

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

}