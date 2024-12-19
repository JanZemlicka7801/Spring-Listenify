package business;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    //CREATE TABLE Genres (
    //                        genre_id INT AUTO_INCREMENT PRIMARY KEY,
    //                        genre_name VARCHAR(50) NOT NULL UNIQUE
    //);
    @EqualsAndHashCode.Include
    @NonNull
    private int genreId;
    private String genre_name;

    public Genre(String genre_name) {
        genreId = 0;
        this.genre_name = genre_name;
    }
}
