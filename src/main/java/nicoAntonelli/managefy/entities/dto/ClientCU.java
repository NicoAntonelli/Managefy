package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// Client for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class ClientCU {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone;
    private Long businessID;
    private Set<Long> salesIDs;
}
