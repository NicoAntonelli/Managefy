package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sales")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Sale {
    // State enum
    public enum SaleState { Cancelled, PendingPayment, PartialPayment, Paid, PaidAndBilled }

    @Id
    @SequenceGenerator(name = "sales_sequence", sequenceName = "sales_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime date;
    @Column(nullable = false)
    private BigDecimal totalPrice;
    private BigDecimal partialPayment; // Nullable
    @Column(nullable = false)
    private SaleState state;
    private String observation; // Nullable

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "businessID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "businesses_sales_fk")
    )
    private Business business;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "clientID",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "clients_sales_fk")
    )
    private Client client; // Nullable

    @ToString.Exclude
    @OneToMany(mappedBy = "sale", cascade = { CascadeType.ALL },
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SaleLine> saleLines = new ArrayList<>();

    public Sale(Long id) {
        this.id = id;
    }

    public Sale(Long id, BigDecimal partialPayment, SaleState state, String observation) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.partialPayment = partialPayment;

        if (state == null) state = SaleState.PendingPayment;
        this.state = state;
        this.observation = observation;
    }

    public Sale(BigDecimal partialPayment, String state, String observation) {
        this.date = LocalDateTime.now();
        this.partialPayment = partialPayment;

        if (!setStateByText(state)) setStateByText("PendingPayment");
        this.observation = observation;
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

    @SuppressWarnings("SpellCheckingInspection")
    public Boolean setStateByText(String state) {
        switch (state.toLowerCase()) {
            case "cancelled" -> setState(Sale.SaleState.Cancelled);
            case "pendingpayment" -> setState(Sale.SaleState.PendingPayment);
            case "partialpayment" -> setState(Sale.SaleState.PartialPayment);
            case "paid" -> setState(Sale.SaleState.Paid);
            case "paidandbilled" -> setState(SaleState.PaidAndBilled);
            default -> { return false; }
        }

        return true;
    }

    public void calculateAndSetTotalPrice() {
        List<SaleLine> lines = getSaleLines();
        if (lines.isEmpty()) this.setTotalPrice(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (SaleLine line : lines) {
            total = total.add(line.getSubtotal());
        }

        this.setTotalPrice(total);
    }

    public void calculateAndSetTotalPrice(List<SaleLine> lines) {
        if (lines.isEmpty()) this.setTotalPrice(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        for (SaleLine line : lines) {
            total = total.add(line.getSubtotal());
        }

        this.setTotalPrice(total);
    }

    // Identity based on ID only
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Sale other = (Sale) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
