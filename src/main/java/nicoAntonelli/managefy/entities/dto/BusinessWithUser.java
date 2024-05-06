package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nicoAntonelli.managefy.entities.Business;

@Data @NoArgsConstructor @AllArgsConstructor
public class BusinessWithUser {
    private Business business;
    private Long userID;
}
