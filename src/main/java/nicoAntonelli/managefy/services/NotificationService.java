package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> GetNotificationsByUser(Long userID) {
        if (userID == null || userID == 0) {
            throw new IllegalStateException("Error at 'GetNotificationsByUser'. User with ID: " + userID + " doesn't exists");
        }

        return notificationRepository.findByUser(userID);
    }

    public Boolean ExistsNotification(Long notificationID) {
        return notificationRepository.existsById(notificationID);
    }

    public Notification GetOneNotification(Long notificationID) {
        Optional<Notification> notification = notificationRepository.findById(notificationID);
        if (notification.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneNotification' - Notification with ID: " + notificationID + " doesn't exists");
        }

        return notification.get();
    }

    public Notification CreateNotification(Notification notification) {
        notification.setId(null);
        notification.setState(Notification.NotificationState.Unread);
        notification.setDate(new Date());

        return notificationRepository.save(notification);
    }

    public Notification UpdateNotificationState(Long notificationID, String state) {
        Notification notification = GetOneNotification(notificationID);
        switch (state.toLowerCase()) {
            case "unread" -> notification.setState(Notification.NotificationState.Unread);
            case "read" -> notification.setState(Notification.NotificationState.Read);
            case "closed" -> notification.setState(Notification.NotificationState.Closed);
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }

        return notification;
    }

    public Long DeleteNotification(Long notificationID) {
        boolean exists = ExistsNotification(notificationID);
        if (!exists) {
            throw new IllegalStateException("Error at 'DeleteNotification' - Notification with ID: " + notificationID + " doesn't exists");
        }

        notificationRepository.deleteById(notificationID);
        return notificationID;
    }
}
