package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

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

    @JsonIgnore
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL })
    private Set<UserRole> userRoles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL },
               orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sale> sales = new ArrayList<>();

    public Business(Long id) {
        this.id = id;
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
