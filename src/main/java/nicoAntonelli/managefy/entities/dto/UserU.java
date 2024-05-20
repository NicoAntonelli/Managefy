package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// User for Update
@Data @NoArgsConstructor @AllArgsConstructor
public class UserU {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Boolean emailNotifications;
}
