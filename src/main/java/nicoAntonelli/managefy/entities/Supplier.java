package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "suppliers")
@Data @NoArgsConstructor @AllArgsConstructor
public class Supplier {
    @Id
    @SequenceGenerator(name = "suppliers_sequence", sequenceName = "suppliers_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suppliers_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String mail;
    @Column(nullable = false)
    private String phone;
    private Date deletionDate; // Nullable

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = { CascadeType.ALL },
               orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    public Supplier(String name, String description, String mail, String phone) {
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.deletionDate = null;
    }

    public Supplier(String name, String description, String mail,
                    String phone, Date detelionDate) {
        this.name = name;
        this.description = description;
        this.mail = mail;
        this.phone = phone;
        this.deletionDate = detelionDate;
    }
}
