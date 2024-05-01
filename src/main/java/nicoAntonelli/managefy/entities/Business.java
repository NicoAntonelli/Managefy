package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@Entity
@Table(name = "businesses")
@Data @NoArgsConstructor @AllArgsConstructor
public class Business {
    @Id
    @SequenceGenerator(name = "businesses_sequence", sequenceName = "businesses_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "businesses_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String link;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private SortedMap<String, Boolean> businessDays = new TreeMap<>(){{
        put("Monday", true);
        put("Tuesday", true);
        put("Wednesday", true);
        put("Thursday", true);
        put("Friday", true);
        put("Saturday", false);
        put("Sunday", false);
    }};

    @ManyToMany(mappedBy = "userRoles")
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
    }

    public Business(String name, String description, String link, SortedMap<String, Boolean> businessDays) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.businessDays = businessDays;
    }

    public Business(String name, String description, String link) {
        this.name = name;
        this.description = description;
        this.link = link;
    }
}
