package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nicoAntonelli.managefy.entities.Client;

import java.util.Set;

// Client for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientCU {
    private Client client;
    private Long businessID;
    private Set<Long> sales;
}
