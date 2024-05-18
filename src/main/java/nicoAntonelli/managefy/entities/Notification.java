package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Notification {
    // Type enum
    public enum NotificationType { Low, Normal, Priority }

    // State enum
    public enum NotificationState { Unread, Read, Closed }

    @Id
    @SequenceGenerator(name = "notifications_sequence", sequenceName = "notifications_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_sequence")
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private NotificationType type;
    @Column(nullable = false)
    private NotificationState state;
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(
            name = "userID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "users_notifications_fk")
    )
    private User user;

    public Notification(Long id) {
        this.id = id;
    }

    public Notification(String description, String type) {
        this.description = description;
        if (!setTypeByText(type)) setTypeByText("low");
        this.state = NotificationState.Unread;
        this.date = LocalDateTime.now();
    }

    public Notification(String description, NotificationType type, NotificationState state,
                        LocalDateTime date, Long userID) {
        this.description = description;
        this.type = type;
        this.state = state;
        this.date = date;

        // Load nested entity with ID-only
        user = new User(userID);
    }

    public void setUserByID(Long userID) {
        user = new User();
        user.setId(userID);
    }

    public Boolean setTypeByText(String type) {
        switch (type.toLowerCase()) {
            case "low" -> setType(Notification.NotificationType.Low);
            case "normal" -> setType(Notification.NotificationType.Normal);
            case "priority" -> setType(Notification.NotificationType.Priority);
            default -> { return false; }
        }

        return true;
    }

    public Boolean setStateByText(String state) {
        switch (state.toLowerCase()) {
            case "unread" -> setState(Notification.NotificationState.Unread);
            case "read" -> setState(Notification.NotificationState.Read);
            case "closed" -> setState(Notification.NotificationState.Closed);
            default -> { return false; }
        }

        return true;
    }
}
