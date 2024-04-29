package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users",
       uniqueConstraints = {
               @UniqueConstraint(name = "users_mail_unique", columnNames = "mail")
       })
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String mail; // Unique
    @Column(nullable = false)
    private String password;
    private String name; // Nullable
    @Column(nullable = false)
    private Boolean validated;
    @Column(nullable = false)
    private Boolean mailNotifications;

    public User(String mail, String password, String name, Boolean validated, Boolean mailNotifications) {
        this.mail = mail;
        this.password = password;
        this.name = name;
        this.validated = validated;
        this.mailNotifications = mailNotifications;
    }
}
