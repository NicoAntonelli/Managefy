package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// SaleLine for Create
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleLineC {
    private Integer position;
    private Integer amount;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal discountSurcharge; // Optional
    private Long productID;
}
