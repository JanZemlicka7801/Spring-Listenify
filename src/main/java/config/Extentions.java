package config;

import business.User;
import persistence.UserDao;
import persistence.UserDaoImpl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Extentions {

    static String answer = "";
    static Scanner sc = new Scanner(System.in);

    static UserDao userDao = new UserDaoImpl("database.properties");

    public static void welcome() {
        System.out.println("Welcome to a brand new and best music storing platform in the world Listenfy! \n" +
                "This platform allows you to view all playlists, artists, songs and their albums. Also you \n" +
                "will be able to create your own playlists and rate songs that are on the platform. Please \n" +
                "before you will be able to access our platform log in or sign in using your credit or \n" +
                "debit card. Since this is a paid platform we need to validate those cards before showing\n" +
                "platform's content. Thank you for using Listenfy!\n");

        System.out.println("L       IIIII  SSSSS   TTTTT  EEEEE  N   N  FFFFF  Y   Y");
        System.out.println("L         I    S         T    E      NN  N  F       Y Y ");
        System.out.println("L         I     SSS      T    EEEE   N N N  FFF      Y ");
        System.out.println("L         I        S     T    E      N  NN  F        Y");
        System.out.println("LLLLL   IIIII  SSSSS     T    EEEEE  N   N  F        Y");
        System.out.println(" ");
    }

    public static void menu() {
        while (true) {
            System.out.println("Please select an option:\n" +
                    "1. Sign in\n" +
                    "2. Register\n" +
                    "3. Exit\n");

            answer = sc.nextLine().trim();

            switch (answer) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("Exiting program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Not a valid option! Please select a valid option again.");
                    break;
            }
        }
    }

    /**
     * Handles the user login process for Listenfy music platform.
     * Prompts user for username and password, validates against database,
     * and navigates to main menu if successful.
     *
     * @throws RuntimeException If there is an error with password hashing
     * @author Malikom12
     */
    public static void login() {
        System.out.print("Enter username: ");
        String username = sc.next();

        System.out.print("Enter password: ");
        String password = sc.next();

        try {
            User user = userDao.login(username, password);
            if (user != null) {
                System.out.println("Login successful! Welcome, " + user.getUsername());
                MainMenu mainMenu = new MainMenu(user, sc);
                mainMenu.displayMenu();
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred during login.");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register() {
        System.out.print("Enter a username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter a password: ");
        String password = sc.nextLine().trim();

        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$#!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!Pattern.matches(passwordPattern, password)) {
            System.out.println("Invalid password format. Please enter a valid password.\n" +
                    "At least one uppercase letter\n" +
                    "At least one lowercase letter\n" +
                    "At least one digit\n" +
                    "At least one special character (e.g., @,$,#,%, etc.)\n" +
                    "A minimum length of 8 characters");
            return;
        }
        System.out.print("Enter an email: ");
        String email = sc.nextLine().trim();

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

        if (!Pattern.matches(emailPattern, email)) {
            System.out.println("Invalid email format. Please enter a valid email.");
            return;
        }

        System.out.print("Enter your credit card number: ");
        String creditCard = sc.nextLine().trim();

        if (!cardService.cardRegister(creditCard)) {
            System.out.println("Invalid credit card. Registration failed.");
            return;
        }

        User newUser = new User(username, password, "", email, LocalDate.now());

        try {
            boolean success = userDao.register(newUser);
            if (success) {
                System.out.println("Registration successful! You can now log in.");
            } else {
                System.out.println("Registration failed. Username or email may already be in use.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("username")) {
                    System.out.println("Registration failed. Username already exists. Please choose a different username.");
                } else if (e.getMessage().contains("email")) {
                    System.out.println("Registration failed. Email already exists. Please use a different email.");
                } else {
                    System.out.println("Registration failed due to a duplicate entry.");
                }
            } else {
                System.out.println("An error occurred during registration. Please try again.");
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("An error occurred with password encryption", e);
        }
    }
}



