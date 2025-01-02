package controllers;

import business.Artist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import persistence.ArtistDao;
import persistence.ArtistDaoImpl;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Controller
public class ArtistController {

    @GetMapping("/artists")
    public String getAllArtists(Model model) {
        ArtistDao artistDao = new ArtistDaoImpl("database.properties");

        try {
            List<Artist> artists = artistDao.getAllArtists();
            model.addAttribute("artists", artists);
            log.info("Successfully retrieved {} artists", artists.size());
            return "artists/list";
        } catch (SQLException e) {
            log.error("Error retrieving artists: {}", e.getMessage());
            model.addAttribute("errMsg", "Failed to retrieve artists");
            return "error";
        }
    }

    @GetMapping("/artists/{id}")
    public String getArtistDetails(@PathVariable("id") int artistId, Model model) {
        // TODO: Implement artist details
        return "artists/details";
    }
}