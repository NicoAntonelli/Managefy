package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.NotificationRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Notification> GetNotifications(User user) {
        return notificationRepository.findByUser(user.getId());
    }

    public Boolean ExistsNotification(Long notificationID, User user) {
        return notificationRepository.existsByIdAndUser(notificationID, user.getId());
    }

    public Notification GetOneNotification(Long notificationID, User user) {
        Optional<Notification> notification = notificationRepository.findByIdAndUser(notificationID, user.getId());
        if (notification.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneNotification' - Notification with ID: " + notificationID + " doesn't exist or is not associated with the user: " + user.getId());
        }

        return notification.get();
    }

    public Notification CreateNotification(Notification notification, User user) {
        // Forced initial state
        notification.setId(null);
        notification.setUser(user);
        notification.setState(Notification.NotificationState.Unread);
        notification.setDate(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    public Notification UpdateNotificationState(Long notificationID, String state, User user) {
        Notification notification = GetOneNotification(notificationID, user);
        Boolean result = notification.setStateByText(state);
        if (!result) {
            throw new Exceptions.BadRequestException("Error at 'UpdateNotificationState' - Unexpected value: " + state);
        }

        return notification;
    }

    public Long DeleteNotification(Long notificationID, User user) {
        boolean exists = ExistsNotification(notificationID, user);
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteNotification' - Notification with ID: " + notificationID + " doesn't exist or is not associated with the user: " + user.getId());
        }

        notificationRepository.deleteById(notificationID);
        return notificationID;
    }
}
