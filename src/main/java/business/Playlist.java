package business;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    //CREATE TABLE Playlists
    //(
    //    playlist_id   INT AUTO_INCREMENT PRIMARY KEY,
    //    user_id       INT          NOT NULL,
    //    playlist_name VARCHAR(100) NOT NULL,
    //    is_public     BOOLEAN   DEFAULT FALSE,
    //    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    //    FOREIGN KEY (user_id) REFERENCES Users (user_id) ON DELETE CASCADE
    //);
    @EqualsAndHashCode.Include
    @NonNull
    private int playlist_id;
    private int user_id;
    private String playlist_name;
    private boolean is_public;
    private LocalDate creation_date;

    public Playlist(int user_id, String playlist_name, boolean is_public, LocalDate creation_date) {
        this.user_id = user_id;
        this.playlist_name = playlist_name;
        this.is_public = is_public;
        this.creation_date = creation_date;
    }
}
