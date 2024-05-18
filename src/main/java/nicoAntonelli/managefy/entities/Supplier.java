package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @Column(columnDefinition = "TEXT")
    private String description; // Nullable
    private String email; // Nullable
    private String phone; // Nullable
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime deletionDate; // Nullable

    @JsonIgnore
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

    public void loadProducts(Set<Long> productsIDs, Long BusinessID) {
        Set<Product> productsForSet = new HashSet<>();
        for (Long id : productsIDs) {
            Product product = new Product(id);
            product.setBusinessByID(BusinessID);

            productsForSet.add(product);
        }

        setProducts(productsForSet);
    }
}
