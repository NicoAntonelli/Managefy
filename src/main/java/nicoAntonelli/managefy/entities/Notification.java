package nicoAntonelli.managefy.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "notifications")
@Data @NoArgsConstructor @AllArgsConstructor
public class Notification {
    // Type enum
    public enum NotificationType { Normal, Important, Priority }

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
    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(
            name = "userID",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "users_notifications_fk")
    )
    private User user;

    public Notification(String description, NotificationType type, NotificationState state,
                        Date date, Long userID) {
        this.description = description;
        this.type = type;
        this.state = state;
        this.date = date;

        // Load nested entity with ID-only
        this.user = new User();
        user.setId(userID);
    }
}
