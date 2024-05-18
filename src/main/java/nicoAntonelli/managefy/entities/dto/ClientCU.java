package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// Client for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientCU {
    private Long id; // Ignored for create
    private String name;
    private String description; // Optional
    private String email; // Optional
    private String phone; // Optional
    private Long businessID;
    private Set<Long> salesIDs;
}
