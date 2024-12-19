package business;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongGenre {
    //CREATE TABLE Song_Genres (
    //                             song_id INT NOT NULL,
    //                             genre_id INT NOT NULL,
    //                             PRIMARY KEY (song_id, genre_id),
    //                             FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE,
    //                             FOREIGN KEY (genre_id) REFERENCES Genres(genre_id) ON DELETE CASCADE
    //);
    @EqualsAndHashCode.Include
    private int genreId;
    @EqualsAndHashCode.Include
    private int songId;
}
