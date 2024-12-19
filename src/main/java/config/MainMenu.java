package config;

import business.User;
import persistence.*;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final User currentUser;
    private final RatingDao ratingDao = new RatingDaoImpl("database.properties");
    private final ArtistDao artistDao = new ArtistDaoImpl("database.properties");
    private final AlbumsDao albumsDao = new AlbumDaoImpl("database.properties");
    private final SongDao songDao = new SongDaoImpl("database.properties");
    private final PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");

    public MainMenu(User user, Scanner scanner) {
        this.currentUser = user;
        this.scanner = scanner;
        scanner.nextLine();
    }

    public void displayMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== LISTENFY MAIN MENU ===");
            System.out.println("1. View all artists");
            System.out.println("2. View artist albums");
            System.out.println("3. View album songs");
            System.out.println("4. Search for songs");
            System.out.println("5. Playlist Management");
            System.out.println("6. Song Ratings");
            System.out.println("7. Logout");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                continue;
            }

            switch (choice) {
                case "1":
                    DaoExtentions.viewAllArtists();
                    break;
                case "2":
                    DaoExtentions.getAlbumsFromArtist();
                    break;
                case "3":
                    DaoExtentions.viewAllSongsInAlbum();
                    break;
                case "4":
                    searchForSong();
                    break;
                case "5":
                    handlePlaylistMenu();
                    break;
                case "6":
                    handleRatingsMenu();
                    break;
                case "7":
                    running = false;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void handlePlaylistMenu() {
        while (true) {
            System.out.println("\n=== PLAYLIST MANAGEMENT ===");
            System.out.println("1. Create new playlist");
            System.out.println("2. View all playlists");
            System.out.println("3. View playlist contents");
            System.out.println("4. Edit playlist");
            System.out.println("5. Return to main menu");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter playlist name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Make playlist public? (y/n): ");
                    boolean isPublic = scanner.nextLine().trim().toLowerCase().startsWith("y");
                    DaoExtentions.createPlaylist(currentUser.getUserId(), name, isPublic);
                    break;
                case "2":
                    DaoExtentions.viewAllPlaylists(currentUser.getUserId());
                    break;
                case "3":
                    System.out.print("Enter playlist name: ");
                    String playlistName = scanner.nextLine().trim();
                    DaoExtentions.viewPlaylistContents(playlistName, currentUser.getUserId());
                    break;
                case "4":
                    handleEditPlaylistMenu();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleEditPlaylistMenu() {
        System.out.println("\n=== EDIT PLAYLIST ===");
        System.out.println("1. Add song");
        System.out.println("2. Remove song");
        System.out.println("3. Rename playlist");
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine().trim();
        System.out.print("Enter playlist name: ");
        String playlistName = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter song title: ");
                String songTitle = scanner.nextLine().trim();
                DaoExtentions.addSongToPlaylist(playlistName, songTitle, currentUser.getUserId());
                break;
            case "2":
                System.out.print("Enter song title: ");
                songTitle = scanner.nextLine().trim();
                DaoExtentions.removeSongFromPlaylist(playlistName, songTitle, currentUser.getUserId());
                break;
            case "3":
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine().trim();
                DaoExtentions.renamePlaylist(playlistName, newName, currentUser.getUserId());
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void handleRatingsMenu() {
        while (true) {
            System.out.println("\n=== SONG RATINGS ===");
            System.out.println("1. Rate a song");
            System.out.println("2. View your rated songs");
            System.out.println("3. View top-rated song");
            System.out.println("4. View most popular song");
            System.out.println("5. Return to main menu");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    RatingService.rateSong(currentUser.getUserId(), this.ratingDao, this.songDao);
                    break;
                case "2":
                    RatingService.viewRatedSongs(currentUser.getUserId(), this.ratingDao);
                    break;
                case "3":
                    RatingService.viewTopRatedSong(this.ratingDao);
                    break;
                case "4":
                    RatingService.viewMostPopularSong(this.ratingDao);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void searchForSong(){
        while (true){
            System.out.println("\n=== SEARCH MENU ===");
            System.out.println("1. Search for song via Title");
            System.out.println("2. Search for song via Artist");
            System.out.println("3. Search for song via Album");
            System.out.println("4. Return to main menu");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    DaoExtentions.searchForSongViaTitle();
                    break;
                case "2":
                    DaoExtentions.searchSongsViaArtists();
                    break;
                case "3":
                    DaoExtentions.searchForSongsViaAlbum();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
