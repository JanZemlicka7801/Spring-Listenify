package listenify.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for managing basic navigation in the Listenify application.
 *
 * Handles requests for login and registration pages.
 */
@Controller
public class IndexController {

    /**
     * Handles requests to the login page.
     *
     * @return the name of the Thymeleaf template for the login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Handles requests to the registration page.
     *
     * @return the name of the Thymeleaf template for the registration page.
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }
}