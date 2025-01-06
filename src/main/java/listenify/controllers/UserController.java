package listenify.controllers;

import jakarta.servlet.http.HttpSession;
import listenify.config.cardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import listenify.business.User;
import listenify.persistence.UserDao;
import listenify.persistence.UserDaoImpl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
@Controller
public class UserController {

    @PostMapping("/register")
    public String registerUser(
            @RequestParam(name="username") String username,
            @RequestParam(name="password") String password,
            @RequestParam(name="email") String email,
            @RequestParam(name="creditCard") String creditCard,
            Model model) {

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            model.addAttribute("errMsg", "All fields must be filled out");
            return "error";
        }

        if (!cardService.cardRegister(creditCard)) {
            model.addAttribute("errMsg", "Invalid credit card");
            return "error";
        }

        UserDao userDao = new UserDaoImpl("database.properties");
        User user = new User(username, password, "", email, LocalDate.now());

        try {
            boolean added = userDao.register(user);
            if (added) {
                model.addAttribute("registeredUser", user);
                log.info("User {} registered successfully", username);
                return "registerSuccess";
            } else {
                String message = "Registration failed for username: " + username;
                model.addAttribute("message", message);
                log.warn("Registration failed for username {}", username);
                return "registerFailed";
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error during registration for username {}: {}", username, e.getMessage());
            model.addAttribute("errMsg", "An error occurred during registration");
            return "error";
        }
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam(name="username") String username,
            @RequestParam(name="password") String password,
            Model model, HttpSession session) {

        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("errMsg", "Username and password cannot be blank");
            return "error";
        }

        UserDao userDao = new UserDaoImpl("database.properties");

        try {
            User user = userDao.login(username, password);
            if (user == null) {
                String message = "Invalid username/password combination";
                model.addAttribute("message", message);
                log.warn("Failed login attempt for username {}", username);
                return "loginFailed";
            }

            session.setAttribute("loggedInUser", user);
            log.info("User {} logged in successfully", username);
            return "redirect:/";

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error during login for username {}: {}", username, e.getMessage());
            model.addAttribute("errMsg", "An error occurred during login");
            return "error";
        }
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            log.info("User {} logged out", user.getUsername());
        }
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @PostMapping("/renewSubscription")
    public String renewSubscription(
            @RequestParam(name="creditCard") String creditCard,
            Model model, HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (!cardService.cardRegister(creditCard)) {
            model.addAttribute("errMsg", "Invalid credit card");
            return "error";
        }

        // TODO: Implement subscription renewal
        log.info("Subscription renewed for user {}", loggedInUser.getUsername());
        model.addAttribute("message", "Subscription renewed successfully");
        return "subscriptionSuccess";
    }


    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", loggedInUser);
        return "editProfile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam String email,
            HttpSession session,
            Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        if (username.isBlank() || email.isBlank()) {
            model.addAttribute("errMsg", "Username and email cannot be blank");
            return "error";
        }

        // Create updated user object
        User updatedUser = new User(
                loggedInUser.getUserId(),
                username,
                loggedInUser.getPassword(),
                loggedInUser.getSalt(),
                email,
                loggedInUser.getRegistrationDate()
        );

        UserDao userDao = new UserDaoImpl("database.properties");
        try {
            boolean updated = userDao.updateUser(updatedUser);
            if (updated) {
                session.setAttribute("loggedInUser", updatedUser);
                log.info("Profile updated for user {}", username);
                return "redirect:/profile";
            } else {
                model.addAttribute("errMsg", "Failed to update profile");
                return "error";
            }
        } catch (SQLException e) {
            log.error("Error updating profile for user {}: {}", username, e.getMessage());
            model.addAttribute("errMsg", "An error occurred while updating profile");
            return "error";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePassword(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        return "changePassword";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            HttpSession session,
            Model model) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        UserDao userDao = new UserDaoImpl("database.properties");
        try {
            boolean passwordChanged = userDao.updatePassword(
                    loggedInUser.getUserId(),
                    currentPassword,
                    newPassword
            );

            if (passwordChanged) {
                log.info("Password changed for user {}", loggedInUser.getUsername());
                return "redirect:/profile";
            } else {
                model.addAttribute("errMsg", "Current password is incorrect");
                return "error";
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error changing password for user {}: {}",
                    loggedInUser.getUsername(), e.getMessage());
            model.addAttribute("errMsg", "An error occurred while changing password");
            return "error";
        }
    }
}