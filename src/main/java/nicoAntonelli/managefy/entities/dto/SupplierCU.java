package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// Supplier for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class SupplierCU {
    private Long id; // Ignored for create
    private String name;
    private String description; // Optional
    private String email; // Optional
    private String phone; // Optional
    private Long businessID;
    private Set<Long> productsIDs;
}
