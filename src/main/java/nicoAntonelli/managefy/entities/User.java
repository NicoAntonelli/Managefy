package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",
       uniqueConstraints = {
               @UniqueConstraint(name = "users_email_unique", columnNames = "email")
       })
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String email; // Unique
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    private String name; // Nullable
    @Column(nullable = false)
    private Boolean validated;
    @Column(nullable = false)
    private Boolean emailNotifications;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL },
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
    private Set<UserRole> userRoles = new HashSet<>();

    public User(Long id) {
        this.id = id;
    }


    public User(Long id, String email, String password, String name, Boolean validated, Boolean emailNotifications) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.validated = validated;
        this.emailNotifications = emailNotifications;
    }

    public User(String email, String password, String name, Boolean validated, Boolean emailNotifications) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.validated = validated;
        this.emailNotifications = emailNotifications;
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.validated = false;
        this.emailNotifications = true;
    }

    // Without password & relations
    public String toStringSafe() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", name=" + name +
                ", validated=" + validated +
                ", emailNotifications=" + emailNotifications +
                "}";
    }

    public static User UserFromJWT(String userStringify) {
        long id = Long.parseLong(userStringify.substring(userStringify.indexOf("id=") + 1, userStringify.indexOf(", email=")));
        String email = userStringify.substring(userStringify.indexOf("email=") + 1, userStringify.indexOf(", name="));
        String name = userStringify.substring(userStringify.indexOf("name=") + 1, userStringify.indexOf(", validated="));
        Boolean validated = Boolean.parseBoolean(userStringify.substring(userStringify.indexOf("validated=") + 1, userStringify.indexOf(", emailNotifications=")));
        Boolean emailNotifications = Boolean.parseBoolean(userStringify.substring(userStringify.indexOf("emailNotifications=") + 1, userStringify.indexOf("}")));

        return new User(id, email, "null", name, validated, emailNotifications);
    }
}
