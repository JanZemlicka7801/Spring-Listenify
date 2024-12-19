package business;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Albums {
    @EqualsAndHashCode.Include
    private int album_id;
    private int artist_id;
    private String album_title;
    private int release_year;

    public Albums(int artist_id, String album_title, int release_year) {
        this.album_id = 0;
        this.artist_id = artist_id;
        this.album_title = album_title;
        this.release_year = release_year;
    }
}
