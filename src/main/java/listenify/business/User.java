package listenify.business;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    //CREATE TABLE Users (
    //                       user_id INT AUTO_INCREMENT PRIMARY KEY,
    //                       username VARCHAR(50) NOT NULL UNIQUE,
    //                       password VARCHAR(255) NOT NULL,
    //                       email VARCHAR(100) NOT NULL UNIQUE,
    //                       registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    //);
    @EqualsAndHashCode.Include
    @NonNull
    private int userId;
    private String username;
    @ToString.Exclude
    private String password;
    private String salt;
    private String email;
    private LocalDate registrationDate;

    public User(String username, String password, String salt, String email, LocalDate registrationDate) {
        userId = 0;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.registrationDate = registrationDate;
    }
}
