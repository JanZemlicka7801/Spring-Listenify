package listenify.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling language changes in the Listenify application.
 */
@Controller
public class LanguageController {

    /**
     * Handles requests to change the application language.
     *
     * This method updates the user's session with the selected language and redirects
     * the user back to the referring page or the home page if no referrer is available.
     *
     * @param lang    the selected language code (e.g., "en" for English, "fr" for French).
     * @param request the {@link HttpServletRequest} object used to access session and request headers.
     * @return a redirect to the referring page or the home page if no referrer is available.
     */
    @GetMapping("/changeLanguage")
    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        // Update the session with the selected language
        request.getSession().setAttribute("lang", lang);

        // Get the referrer URL from the request headers
        String referer = request.getHeader("Referer");

        // Redirect to the referrer or the home page if no referrer is available
        return "redirect:" + (referer != null ? referer : "/");
    }
}