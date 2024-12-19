package business;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistSong {
    //CREATE TABLE Playlist_Songs (
    //                                playlist_id INT NOT NULL,
    //                                song_id INT NOT NULL,
    //                                PRIMARY KEY (playlist_id, song_id),
    //                                FOREIGN KEY (playlist_id) REFERENCES Playlists(playlist_id) ON DELETE CASCADE,
    //                                FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE
    //);
    @EqualsAndHashCode.Include
    private int playlistId;
    @EqualsAndHashCode.Include
    private int songId;
}
