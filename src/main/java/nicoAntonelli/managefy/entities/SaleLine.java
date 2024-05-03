package nicoAntonelli.managefy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "saleLines")
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLine {
    @EmbeddedId
    @Column(updatable = false)
    SaleLineKey id;

    @Column(nullable = false)
    private Float amount;
    @Column(nullable = false)
    private Float price;
    @Column(nullable = false)
    private Boolean discountSurcharge;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "saleID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_saleLines_fk")
    )
    private Sale sale;

    @ManyToOne
    @JoinColumn(
            name = "productID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "sales_products_fk")
    )
    private Product product;

    public SaleLine(Float amount, Float price, Boolean discountSurcharge) {
        this.amount = amount;
        this.price = price;
        this.discountSurcharge = discountSurcharge;
    }
}
