package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "suppliers")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Supplier {
    @Id
    @SequenceGenerator(name = "suppliers_sequence", sequenceName = "suppliers_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suppliers_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description; // Nullable
    private String email; // Nullable
    private String phone; // Nullable
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime deletionDate; // Nullable

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "supplier", cascade = { CascadeType.ALL },
               orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    public Supplier(Long id) {
        this.id = id;
    }

    public Supplier(String name, String description, String email, String phone) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.deletionDate = null;
    }

    public Supplier(String name, String description, String email,
                    String phone, LocalDateTime deletionDate) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.deletionDate = deletionDate;
    }

    // Identity based on ID only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Supplier other = (Supplier) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
