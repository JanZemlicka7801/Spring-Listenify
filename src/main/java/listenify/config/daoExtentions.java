package listenify.config;

import listenify.business.Albums;
import listenify.business.Playlist;
import listenify.business.Song;
import listenify.persistence.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class daoExtentions {


    /**
     * Gets and displays all albums for a specified artist.
     * @auther Seb Mathews-Lynch
     */
    public static void getAlbumsFromArtist(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter artist's first name (leave blank if single name or just has band name): ");
        String firstName = sc.nextLine().trim();
        if (firstName.isEmpty()) {
            firstName = null;
        }
        System.out.print("Enter artist's last name: ");
        String lastName = sc.nextLine().trim();

        AlbumDaoImpl albumsDao = new AlbumDaoImpl("database.properties");

        List<Albums> albums = albumsDao.getAlbumsByArtistName(firstName, lastName);

        if (albums.isEmpty()) {
            System.out.println("No albums found for the specified artist.");
        } else {
            for (Albums album : albums) {
                System.out.println("Title: " + album.getAlbum_title() +
                        ", Release Year: " + album.getRelease_year());
            }
        }
    }
    /**
     * Prompts user for an album title and lists all songs in that album.
     * @auther Seb Mathews-Lynch
     */
    public static void viewAllSongsInAlbum(){
        Scanner scanner = new Scanner(System.in);
        AlbumsDao albumDao = new AlbumDaoImpl("database.properties");
        SongDao songDao = new SongDaoImpl("database.properties");

        String albumTitle;

        while (true) {
            System.out.print("Enter album title (alphabetic characters only): ");
            albumTitle = scanner.nextLine().trim();

            if (albumTitle.matches("[a-zA-Z\\s]+")) {
                break;
            } else {
                System.out.println("Invalid input. Please enter alphabetic characters only.");
            }
        }
        int albumId = albumDao.getAlbumIdByAlbumTitle(albumTitle);

        if (albumId != -1) {
            System.out.println("Album ID for \"" + albumTitle + "\": " + albumId);

            List<Song> songs = songDao.getAllSongsByAlbumId(albumId);

            if (!songs.isEmpty()) {
                System.out.println("\nSongs in album \"" + albumTitle + "\":");
                for (Song song : songs) {
                    System.out.println("Title: " + song.getSongTitle() +
                            ", Duration: " + song.getDuration());
                }
            } else {
                System.out.println("No songs found in the album \"" + albumTitle + "\".");
            }
        } else {
            System.out.println("Album titled \"" + albumTitle + "\" not found.");
        }
    }
    /**
     * Searches and displays information about a song by its title.
     * @auther Seb Mathews-Lynch
     */
    public static void searchForSongViaTitle() {
        SongDao songDao = new SongDaoImpl("database.properties");
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the song title to search: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Please enter a valid title.");
            return;
        }

        Song song = songDao.getSongByTitle(title);

        if (song == null) {
            System.out.println("No song found with the title: " + title);
        } else {
            System.out.println("Song found with the title '" + title + "':");
            System.out.println("Song ID: " + song.getSongId() +
                    ", Title: " + song.getSongTitle() +
                    ", Duration: " + song.getDuration());
        }
    }
    /**
     * Searches for songs in a specified album and displays them.
     * @auther Seb Mathews-Lynch
     */
    public static void searchForSongsViaAlbum() {
        SongDao songDao = new SongDaoImpl("database.properties");
        AlbumsDao albumDao = new AlbumDaoImpl("database.properties");
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the album title to search songs: ");
        String albumTitle = sc.nextLine().trim();

        if (albumTitle.isEmpty()) {
            System.out.println("Album title cannot be empty. Please enter a valid title.");
            return;
        }

        int albumId = albumDao.getAlbumIdByAlbumTitle(albumTitle);
        if (albumId == -1) {
            System.out.println("No album found with the title: " + albumTitle);
            return;
        }

        List<Song> songs = songDao.getAllSongsByAlbumId(albumId);

        if (songs.isEmpty()) {
            System.out.println("No songs found in the album: " + albumTitle);
        } else {
            System.out.println("Songs in the album '" + albumTitle + "':");
            for (Song song : songs) {
                System.out.println("Song ID: " + song.getSongId() +
                        ", Title: " + song.getSongTitle() +
                        ", Duration: " + song.getDuration());
            }
        }
    }
    /**
     * Searches for and lists songs by a specified artist.
     * @auther Seb Mathews-Lynch
     */
    public static void searchSongsViaArtists(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter artist's first name (leave blank if the artist has a single or band name): ");
        String firstName = sc.nextLine().trim();
        if (firstName.isEmpty()) {
            firstName = null;
        }

        System.out.print("Enter artist's last name: ");
        String lastName = sc.nextLine().trim();

        SongDao songDao = new SongDaoImpl("database.properties");

        List<Song> songs = songDao.searchSongsByArtist(firstName, lastName);

        if (songs.isEmpty()) {
            System.out.println("No songs found for the specified artist.");
        } else {
            System.out.println("Songs by " + (firstName != null ? firstName + " " : "") + lastName + ":");
            for (Song song : songs) {
                System.out.println("Title: " + song.getSongTitle() +
                        ", Album ID: " + song.getAlbumId() +
                        ", Duration: " + song.getDuration());
            }
        }
    }

    /**
     * Creates a new playlist for a user.
     *
     * @param userId The ID of the user creating the playlist
     * @param name The name of the playlist
     * @param isPublic True if the playlist should be public, false if private
     * @author Malikom12
     */
    public static void createPlaylist(int userId, String name, boolean isPublic) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        try {
            if (playlistDao.createPlaylist(userId, name, isPublic)) {
                System.out.println("Playlist created successfully.");
            } else {
                System.out.println("Failed to create playlist.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating playlist: " + e.getMessage());
        }
    }

    /**
     * Displays all playlists accessible to a user - their own playlists and public playlists.
     *
     * @param userId The ID of the user viewing the playlists
     * @author Malikom12
     */
    public static void viewAllPlaylists(int userId) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        try {
            List<Playlist> userPlaylists = playlistDao.getUserPlaylists(userId);
            List<Playlist> publicPlaylists = playlistDao.getPublicPlaylists();

            System.out.println("\n=== Your Playlists ===");
            if (userPlaylists.isEmpty()) {
                System.out.println("You haven't created any playlists yet.");
            } else {
                System.out.printf("%-5s %-30s %-10s%n", "ID", "Name", "Public");
                System.out.println("─".repeat(50));
                for (Playlist playlist : userPlaylists) {
                    System.out.printf("%-5d %-30s %-10s%n",
                            playlist.getPlaylist_id(),
                            playlist.getPlaylist_name(),
                            playlist.is_public() ? "Yes" : "No"
                    );
                }
            }

            System.out.println("\n=== Public Playlists ===");
            if (publicPlaylists.isEmpty()) {
                System.out.println("No public playlists available.");
            } else {
                System.out.printf("%-5s %-30s%n", "ID", "Name");
                System.out.println("─".repeat(40));
                for (Playlist playlist : publicPlaylists) {
                    if (playlist.getUser_id() != userId) {
                        System.out.printf("%-5d %-30s%n",
                                playlist.getPlaylist_id(),
                                playlist.getPlaylist_name()
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting playlists: " + e.getMessage());
        }
    }

    /**
     * Shows all songs in a specified playlist if the user has access to view it.
     *
     * @param playlistName The name of the playlist to view
     * @param userId The ID of the user trying to view the playlist
     * @author Malikom12
     */
    public static void viewPlaylistContents(String playlistName, int userId) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        try {
            Playlist playlist = playlistDao.getPlaylistByName(playlistName);
            if (playlist == null) {
                System.out.println("Playlist not found.");
                return;
            }

            if (!playlistDao.canUserAccessPlaylist(playlist.getPlaylist_id(), userId)) {
                System.out.println("You don't have permission to view this playlist.");
                return;
            }

            List<Song> songs = playlistDao.getPlaylistSongs(playlist.getPlaylist_id());
            if (songs.isEmpty()) {
                System.out.println("This playlist is empty.");
                return;
            }

            System.out.printf("\n=== Songs in %s ===\n", playlist.getPlaylist_name());
            System.out.printf("%-30s %s%n", "Song Title", "Duration");
            System.out.println("─".repeat(50));

            for (Song song : songs) {
                System.out.printf("%-30s %s%n",
                        song.getSongTitle(),
                        song.getDuration()
                );
            }
        } catch (SQLException e) {
            System.out.println("Error viewing playlist: " + e.getMessage());
        }
    }

    /**
     * Adds a song to a playlist if the user owns the playlist.
     *
     * @param playlistName The name of the playlist to add to
     * @param songTitle The title of the song to add
     * @param userId The ID of the user trying to add the song
     * @author Malikom12
     */
    public static void addSongToPlaylist(String playlistName, String songTitle, int userId) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        SongDao songDao = new SongDaoImpl("database.properties");
        try {
            Playlist playlist = playlistDao.getPlaylistByName(playlistName);
            if (playlist == null) {
                System.out.println("Playlist not found.");
                return;
            }

            if (!playlistDao.isPlaylistOwner(playlist.getPlaylist_id(), userId)) {
                System.out.println("You can only modify your own playlists.");
                return;
            }

            Song song = songDao.getSongByTitle(songTitle);
            if (song == null) {
                System.out.println("Song not found.");
                return;
            }

            if (playlistDao.addSongToPlaylist(playlist.getPlaylist_id(), song.getSongId())) {
                System.out.println("Song added to playlist successfully.");
            } else {
                System.out.println("Failed to add song to playlist.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding song to playlist: " + e.getMessage());
        }
    }


    /**
     * Removes a song from a playlist if the user owns the playlist.
     *
     * @param playlistName The name of the playlist to remove from
     * @param songTitle The title of the song to remove
     * @param userId The ID of the user trying to remove the song
     * @author Malikom12
     */
    public static void removeSongFromPlaylist(String playlistName, String songTitle, int userId) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        SongDao songDao = new SongDaoImpl("database.properties");
        try {
            Playlist playlist = playlistDao.getPlaylistByName(playlistName);
            if (playlist == null) {
                System.out.println("Playlist not found.");
                return;
            }

            if (!playlistDao.isPlaylistOwner(playlist.getPlaylist_id(), userId)) {
                System.out.println("You can only modify your own playlists.");
                return;
            }

            Song song = songDao.getSongByTitle(songTitle);
            if (song == null) {
                System.out.println("Song not found.");
                return;
            }

            if (playlistDao.removeSongFromPlaylist(playlist.getPlaylist_id(), song.getSongId())) {
                System.out.println("Song removed from playlist successfully.");
            } else {
                System.out.println("Failed to remove song from playlist.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing song from playlist: " + e.getMessage());
        }
    }

    /**
     * Renames a playlist if the user owns it.
     *
     * @param oldName Current name of the playlist
     * @param newName New name for the playlist
     * @param userId The ID of the user trying to rename the playlist
     * @author Malikom12
     */
    public static void renamePlaylist(String oldName, String newName, int userId) {
        PlaylistDao playlistDao = new PlaylistDaoImpl("database.properties");
        try {
            Playlist playlist = playlistDao.getPlaylistByName(oldName);
            if (playlist == null) {
                System.out.println("Playlist not found.");
                return;
            }

            if (!playlistDao.isPlaylistOwner(playlist.getPlaylist_id(), userId)) {
                System.out.println("You can only rename your own playlists.");
                return;
            }

            if (playlistDao.renamePlaylist(playlist.getPlaylist_id(), newName)) {
                System.out.println("Playlist renamed successfully.");
            } else {
                System.out.println("Failed to rename playlist.");
            }
        } catch (SQLException e) {
            System.out.println("Error renaming playlist: " + e.getMessage());
        }
    }

}