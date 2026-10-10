package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@IdClass(SaleLineKey.class)
@Table(name = "saleLines")
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class SaleLine {
    @Id
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "saleID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_saleLines_fk")
    )
    private Sale sale;

    @Id
    @Column(updatable = false)
    private Integer position;

    @Column(nullable = false)
    private Integer amount;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private BigDecimal cost;
    private BigDecimal discountSurcharge; // Nullable
    @Transient
    private BigDecimal subtotal; // Calculated

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "productID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_products_fk")
    )
    private Product product;

    public SaleLine(Sale sale, Integer position) {
        this.sale = sale;
        this.position = position;
    }

    public SaleLine(Sale sale, Integer position, Integer amount, BigDecimal price, BigDecimal cost, BigDecimal discountSurcharge) {
        this.sale = sale;
        this.position = position;
        this.amount = amount;
        this.price = price;
        this.cost = cost;

        if (discountSurcharge == null) discountSurcharge = BigDecimal.ONE;
        this.discountSurcharge = discountSurcharge;

        calculateAndSetSubtotal();
    }

    public SaleLine(Integer amount, BigDecimal price, BigDecimal cost, BigDecimal discountSurcharge) {
        this.amount = amount;
        this.price = price;
        this.cost = cost;

        if (discountSurcharge == null) discountSurcharge = BigDecimal.ONE;
        this.discountSurcharge = discountSurcharge;

        calculateAndSetSubtotal();
    }

    public SaleLineKey getId() {
        return new SaleLineKey(getSale().getId(), getPosition());
    }

    public void setSaleByID(Long saleID) {
        sale = new Sale();
        sale.setId(saleID);
    }

    public void setProductByID(Long productID) {
        product = new Product();
        product.setId(productID);
    }

    public void calculateAndSetSubtotal() {
        if (discountSurcharge == null) discountSurcharge = BigDecimal.ONE;
        if (price == null) price = getProduct().getUnitPrice();

        subtotal = BigDecimal.valueOf(amount).multiply(price).multiply(discountSurcharge);
    }

    // Identity based on composite key (sale + position)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SaleLine other = (SaleLine) o;
        if (getSale() == null || other.getSale() == null) return false;
        return getSale().getId() != null && getPosition() != null
                && Objects.equals(getSale().getId(), other.getSale().getId())
                && Objects.equals(getPosition(), other.getPosition());
    }

    @Override
    public int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
