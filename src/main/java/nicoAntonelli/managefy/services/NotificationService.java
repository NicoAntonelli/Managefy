package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.NotificationC;
import nicoAntonelli.managefy.repositories.NotificationRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService; // Dependency

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    public List<Notification> GetNotifications(User user) {
        return notificationRepository.findByUser(user.getId());
    }

    public Notification GetOneNotification(Long notificationID, User user) {
        Optional<Notification> notification = notificationRepository.findByIdAndUser(notificationID, user.getId());
        if (notification.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneNotification' - Notification with ID: " + notificationID + " doesn't exist or is not associated with the user: " + user.getId());
        }

        return notification.get();
    }

    public Notification CreateNotification(NotificationC notificationC, User user) {
        String description = notificationC.getDescription();
        if (description == null || description.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateNotification' - Description field was not supplied");
        }

        String type = notificationC.getType();
        if (type == null || type.isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateNotification' - Type field was not supplied");
        }
        if (!List.of("low", "normal", "priority").contains(type.toLowerCase())) {
            throw new Exceptions.BadRequestException("Error at 'CreateNotification' - Unexpected type value: " + type);
        }

        Notification notification = new Notification(description, type);
        notification.setUserByID(user.getId());

        // Save notification
        notification = notificationRepository.save(notification);

        // Optional - Send notifications by email (Ignore exception if fails)
        if (user.getEmailNotifications()) {
            try {
                emailService.NotificationEmail(user.getEmail(), notification);
            }
            catch (Exception _) { }
        }

        return notification;
    }

    public Notification UpdateNotificationState(Long notificationID, String state, User user) {
        Notification notification = GetOneNotification(notificationID, user);
        Boolean result = notification.setStateByText(state);
        if (!result) {
            throw new Exceptions.BadRequestException("Error at 'UpdateNotificationState' - Unexpected value: " + state);
        }

        return notification;
    }

    // Logic deletion (field: notification date)
    public Long CloseNotification(Long notificationID, User user) {
        Notification notification = GetOneNotification(notificationID, user);
        notification.setState(Notification.NotificationState.Closed);
        notificationRepository.save(notification);

        return notificationID;
    }
}
