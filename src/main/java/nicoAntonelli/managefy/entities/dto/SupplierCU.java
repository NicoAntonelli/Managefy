package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nicoAntonelli.managefy.entities.Supplier;

import java.util.Set;

// Supplier for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class SupplierCU {
    private Supplier supplier;
    private Long businessID;
    private Set<Long> products;
}
