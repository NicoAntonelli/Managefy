package nicoAntonelli.managefy.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Notification for Create
@Data @NoArgsConstructor @AllArgsConstructor
public class NotificationC {
    private String description;
    private String type;
}
