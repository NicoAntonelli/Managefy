package nicoAntonelli.managefy.entities;

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
    private Float partialPayment; // Nullable
    @Column(nullable = false)
    private SaleState saleState;

    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "business_sales_fk")
    )
    private Business business;

    @ManyToOne
    @JoinColumn(
            name = "clientID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "clients_sales_fk")
    )
    private Client client;

    @OneToMany(mappedBy = "sale", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleLine> saleLines = new ArrayList<>();

    public Sale(Long id, Float partialPayment, SaleState saleState) {
        this.id = id;
        this.date = new Date();
        this.partialPayment = partialPayment;

        if (saleState == null) saleState = SaleState.PendingPayment;
        this.saleState = saleState;
    }

    public Sale(Float partialPayment, SaleState saleState) {
        this.date = new Date();
        this.partialPayment = partialPayment;

        if (saleState == null) saleState = SaleState.PendingPayment;
        this.saleState = saleState;
    }

    public void setBusinessByID(Long businessID) {
        business = new Business();
        business.setId(businessID);
    }

    public void setClientByID(Long clientID) {
        client = new Client();
        client.setId(clientID);
    }

    public void addSaleLine(SaleLine saleLine) {
        saleLines.add(saleLine);
    }

    public float calculateTotalPrice() {
        List<SaleLine> lines = getSaleLines();
        if (lines.isEmpty()) return 0;

        float total = 0;
        for (SaleLine line : lines) {
            total += line.getSubtotal();
        }

        return total;
    }
}
