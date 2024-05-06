package nicoAntonelli.managefy.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserRoleKey {
    private Long user;
    private Long business;
}
