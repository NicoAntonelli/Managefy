package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// Sale for Create
@Data @NoArgsConstructor @AllArgsConstructor
public class SaleC {
    private String state;
    private BigDecimal partialPayment; // Optional
    private Long businessID;
    private ClientCU client; // Optional
    private List<SaleLineC> saleLines;
}
