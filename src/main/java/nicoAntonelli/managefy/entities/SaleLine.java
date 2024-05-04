package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saleLines")
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLine {
    @EmbeddedId
    @Column(updatable = false)
    SaleLineKey id;

    @Column(nullable = false)
    private Integer amount;
    @Column(nullable = false)
    private Float price;
    @Transient
    private Float subtotal;
    private Float discountSurcharge; // Nullable

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "sale",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_saleLines_fk")
    )
    private Sale sale;

    @ManyToOne
    @JoinColumn(
            name = "product",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_products_fk")
    )
    private Product product;

    public SaleLine(SaleLineKey id, Integer amount, Float price, Float discountSurcharge) {
        this.id = id;
        this.amount = amount;
        this.price = price;

        if (discountSurcharge == null) discountSurcharge = 1f;
        this.discountSurcharge = discountSurcharge;

        calculateAndSetSubtotal();
    }

    public SaleLine(Integer amount, Float price, Float discountSurcharge) {
        this.amount = amount;
        this.price = price;

        if (discountSurcharge == null) discountSurcharge = 1f;
        this.discountSurcharge = discountSurcharge;

        calculateAndSetSubtotal();
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
        if (discountSurcharge == null) discountSurcharge = 1f;
        subtotal = amount * price * discountSurcharge;
    }
}
