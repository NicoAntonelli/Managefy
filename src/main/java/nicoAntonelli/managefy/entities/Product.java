package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Data @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @SequenceGenerator(name = "products_sequence", sequenceName = "products_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private BigDecimal unitCost;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private Integer stock;
    private Integer stockMin; // Nullable
    private Integer saleMinAmount; // Nullable
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime deletionDate; // Nullable

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "businesses_products_fk")
    )
    private Business business;

    @ManyToOne
    @JoinColumn(
            name = "supplierID",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "suppliers_products_fk")
    )
    private Supplier supplier; // Nullable

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SaleLine> saleLines = new HashSet<>();

    public Product(Long id) {
        this.id = id;
    }

    public Product(String code, String name, String description, BigDecimal unitCost, BigDecimal unitPrice,
                   Integer stock, Integer stockMin, Integer saleMinAmount) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.stock = stock;
        this.stockMin = stockMin;
        this.saleMinAmount = saleMinAmount;
        this.deletionDate = null;
    }

    public Product(String code, String name, String description, BigDecimal unitCost, BigDecimal unitPrice,
                   Integer stock, Integer stockMin, Integer saleMinAmount, LocalDateTime deletionDate) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.unitCost = unitCost;
        this.unitPrice = unitPrice;
        this.stock = stock;
        this.stockMin = stockMin;
        this.saleMinAmount = saleMinAmount;
        this.deletionDate = deletionDate;
    }

    public void setBusinessByID(Long businessID) {
        business = new Business();
        business.setId(businessID);
    }

    public void setSupplierByID(Long supplierID) {
        supplier = new Supplier();
        supplier.setId(supplierID);
    }
}
