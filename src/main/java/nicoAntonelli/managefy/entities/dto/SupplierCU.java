package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// Supplier for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class SupplierCU {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private Long businessID;
    private Set<Long> productsIDs;
}
