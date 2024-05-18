package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

// Product for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductCU {
    private Long id; // Ignored for create
    private String code;
    private String name;
    private String description;
    private BigDecimal unitCost;
    private BigDecimal unitPrice;
    private Integer stock;
    private Integer stockMin; // Optional
    private Integer saleMinAmount; // Optional
    private Long businessID;
    private SupplierCU supplier; // Optional
}
