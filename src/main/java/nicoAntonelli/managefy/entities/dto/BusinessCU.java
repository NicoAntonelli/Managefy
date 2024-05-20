package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.SortedMap;

// Business for Create & Update
@Data @NoArgsConstructor @AllArgsConstructor
public class BusinessCU {
    private Long id; // Ignored for create
    private String name;
    private String description;
    private String link;
    private Boolean isPublic;
    private SortedMap<String, Boolean> businessDays;
}
