package business;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    //CREATE TABLE Ratings (
    //                         user_id INT NOT NULL,
    //                         song_id INT NOT NULL,
    //                         rating INT CHECK (rating BETWEEN 1 AND 5),
    //                         PRIMARY KEY (user_id, song_id),
    //                         FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    //                         FOREIGN KEY (song_id) REFERENCES Songs(song_id) ON DELETE CASCADE
    //);
    @EqualsAndHashCode.Include
    private int userId;
    @EqualsAndHashCode.Include
    private int songId;
    private int rating;
}
