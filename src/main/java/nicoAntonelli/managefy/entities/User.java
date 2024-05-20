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

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = { CascadeType.ALL })
    private UserValidation userValidation;

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", validated=" + validated +
                ", emailNotifications=" + emailNotifications +
                '}';
    }

    public static User UserFromJWT(String payload) {
        List<String> filters = List.of("id=", "email=", "name=", "validated=", "emailNotifications=");
        String id = payload.substring(payload.indexOf(filters.get(0)) + filters.get(0).length(), payload.indexOf(filters.get(1)) - 2);
        String email = payload.substring(payload.indexOf(filters.get(1)) + filters.get(1).length(), payload.indexOf(filters.get(2)) - 2);
        String name = payload.substring(payload.indexOf(filters.get(2)) + filters.get(2).length(), payload.indexOf(filters.get(3)) - 2);
        String validated = payload.substring(payload.indexOf(filters.get(3)) + filters.get(3).length(), payload.indexOf(filters.get(4)) - 2);
        String emailNotifications = payload.substring(payload.indexOf(filters.get(4)) + filters.get(4).length(), payload.indexOf("}"));

        return new User(Long.parseLong(id), email, null, name, Boolean.parseBoolean(validated), Boolean.parseBoolean(emailNotifications));
    }
}
