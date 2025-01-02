package controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/artists")
    public String artists() {
        return "artists";
    }

    @GetMapping("/albums")
    public String albums() {
        return "albums";
    }

    @GetMapping("/songs")
    public String songs() {
        return "songs";
    }

    @GetMapping("/playlists")
    public String playlists() {
        return "playlists";
    }
}