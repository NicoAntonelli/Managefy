package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nicoAntonelli.managefy.entities.*;

import java.util.List;

// Full resources for a public business display
@Data @NoArgsConstructor @AllArgsConstructor
public class BusinessResources {
    private Long businessID;
    private List<Client> clients;
    private List<Product> products;
    private List<Sale> sales;
    private List<Supplier> suppliers;
    private List<UserRole> userRoles;
}
