package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.entities.User;
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
    private final UserService userService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService,
                               ErrorLogService errorLogService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.errorLogService = errorLogService;
    }

    public List<Notification> GetNotificationsByUser(Long userID) {
        if (userID == null || userID == 0) {
            throw new IllegalStateException("Error at 'GetNotificationsByUser'. User with ID: " + userID + " doesn't exist");
        }

        return notificationRepository.findByUser(userID);
    }

    public Boolean ExistsNotification(Long notificationID) {
        return notificationRepository.existsById(notificationID);
    }

    public Notification GetOneNotification(Long notificationID) {
        try {
            Optional<Notification> notification = notificationRepository.findById(notificationID);
            if (notification.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneNotification' - Notification with ID: " + notificationID + " doesn't exist");
            }

            return notification.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Notification CreateNotification(Notification notification) {
        try {
            // Validate associated user
            User user = notification.getUser();
            if (user == null || user.getId() == null) {
                throw new IllegalStateException("Error at 'CreateNotification' - User not supplied");
            }
            if (!userService.ExistsUser(user.getId())) {
                throw new IllegalStateException("Error at 'CreateNotification' - User with ID: " + user.getId() + " doesn't exist");
            }

            notification.setId(null);
            notification.setState(Notification.NotificationState.Unread);
            notification.setDate(new Date());

            return notificationRepository.save(notification);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Notification UpdateNotificationState(Long notificationID, String state) {
        try {
            Notification notification = GetOneNotification(notificationID);
            Boolean result = notification.setStateByText(state);
            if (!result) {
                throw new IllegalStateException("Error at 'UpdateNotificationState' - Unexpected value: " + state);
            }

            return notification;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Long DeleteNotification(Long notificationID) {
        try {
            boolean exists = ExistsNotification(notificationID);
            if (!exists) {
                throw new IllegalStateException("Error at 'DeleteNotification' - Notification with ID: " + notificationID + " doesn't exist");
            }

            notificationRepository.deleteById(notificationID);
            return notificationID;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
