package listenify.business;

import lombok.*;

import java.sql.Time;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    //CREATE TABLE Songs (
    //                       song_id INT AUTO_INCREMENT PRIMARY KEY,
    //                       album_id INT NOT NULL,
    //                       song_title VARCHAR(100) NOT NULL,
    //                       duration TIME,
    //                       FOREIGN KEY (album_id) REFERENCES Albums(album_id) ON DELETE CASCADE
    //);
    @EqualsAndHashCode.Include
    private int songId;
    private int albumId;
    private String songTitle;
    private Time duration;

    public Song(int albumId, String songTitle, Time duration) {
        songId = 0;
        this.albumId = albumId;
        this.songTitle = songTitle;
        this.duration = duration;
    }
}