package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sales")
@Data @NoArgsConstructor @AllArgsConstructor
public class Sale {
    // State enum
    public enum SaleState { Canceled, PendingPayment, PartialPayment, Payed, PayedAndBilled }

    @Id
    @SequenceGenerator(name = "sales_sequence", sequenceName = "sales_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private Float totalPrice;
    private float partialPayment; // Nullable
    @Column(nullable = false)
    private SaleState saleState;

    @ManyToOne
    @JoinColumn(
            name = "clientID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "clients_sales_fk")
    )
    private Client client;

    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "business_sales_fk")
    )
    private Business business;

    @OneToMany(mappedBy = "sale", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleLine> saleLines = new ArrayList<>();

    public Sale(Date date, Float totalPrice, float partialPayment, SaleState saleState) {
        this.date = date;
        this.totalPrice = totalPrice;
        this.partialPayment = partialPayment;
        this.saleState = saleState;
    }
}
