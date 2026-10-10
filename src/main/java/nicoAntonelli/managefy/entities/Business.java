package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(name = "businesses",
        uniqueConstraints = {
                @UniqueConstraint(name = "businesses_link_unique", columnNames = "link")
        })
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
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
    private String link; // Unique
    @Column(nullable = false)
    private Boolean isPublic;

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

    @Transient
    private String currentUserRole; // Nullable, not persisted in DB

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL })
    private Set<UserRole> userRoles = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL },
               orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "business", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sale> sales = new ArrayList<>();

    public Business(Long id) {
        this.id = id;
    }

    public Business(String name, String description, String link,
                    Boolean isPublic, SortedMap<String, Boolean> businessDays) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.isPublic = isPublic;
        this.businessDays = businessDays;
    }

    public Business(String name, String description, String link, Boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.link = link;
    }

    // Identity based on ID only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Business other = (Business) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
