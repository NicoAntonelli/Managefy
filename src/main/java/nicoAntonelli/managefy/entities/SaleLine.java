package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@IdClass(SaleLineKey.class)
@Table(name = "saleLines")
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLine {
    @Id
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
    Integer position;

    @Column(nullable = false)
    private Integer amount;
    @Column(nullable = false)
    private Float price;
    @Transient
    private Float subtotal;
    private Float discountSurcharge; // Nullable

    @ManyToOne
    @JoinColumn(
            name = "productID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_products_fk")
    )
    private Product product;

    public SaleLine(Sale sale, Integer position, Integer amount, Float price, Float discountSurcharge) {
        this.sale = sale;
        this.position = position;
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
        if (discountSurcharge == null) discountSurcharge = 1f;
        subtotal = amount * price * discountSurcharge;
    }
}
