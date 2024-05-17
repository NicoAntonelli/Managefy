package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@IdClass(SaleLineKey.class)
@Table(name = "saleLines")
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLine {
    @Id
    @JsonIgnore
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
}
