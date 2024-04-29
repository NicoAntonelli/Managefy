package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping()
    public List<Notification> GetNotifications(@RequestParam Long userID) {
        return notificationService.GetNotificationsByUser(userID);
    }

    @GetMapping(path = "{notificationID}")
    public Notification GetOneNotification(@PathVariable("notificationID") Long notificationID) {
        return notificationService.GetOneNotification(notificationID);
    }

    @PostMapping
    public Notification CreateNotification(@RequestBody Notification notification) {
        notification.setId(null);
        notification.setState(Notification.NotificationState.Unread);
        notification.setDate(new Date());

        return notificationService.CreateNotification(notification);
    }

    @PutMapping(path = "{notificationID}")
    public Notification UpdateNotification(@PathVariable("notificationID") Long notificationID,
                                           @RequestParam String state) {
        return notificationService.UpdateNotificationState(notificationID, state);
    }

    @DeleteMapping(path = "{notificationID}")
    public Long DeleteNotification(@PathVariable("notificationID") Long notificationID) {
        return notificationService.DeleteNotification(notificationID);
    }
}
