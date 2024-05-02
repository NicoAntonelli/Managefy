package nicoAntonelli.managefy.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class UserRoleKey implements Serializable {
    private Long userID;
    private Long businessID;
}
