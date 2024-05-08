package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.entities.helpTypes.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public NotificationController(NotificationService notificationService, ErrorLogService errorLogService) {
        this.notificationService = notificationService;
        this.errorLogService = errorLogService;
    }

    @GetMapping(path = "/user/{userID}")
    public Result<List<Notification>> GetNotificationsByUser(@PathVariable("userID") Long userID) {
        try {
            List<Notification> notifications = notificationService.GetNotificationsByUser(userID);
            return new Result<>(notifications);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @GetMapping(path = "{notificationID}")
    public Result<Notification> GetOneNotification(@PathVariable("notificationID") Long notificationID) {
        try {
            Notification notification = notificationService.GetOneNotification(notificationID);
            return new Result<>(notification);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PostMapping
    public Result<Notification> CreateNotification(@RequestBody Notification notification) {
        try {
            notification = notificationService.CreateNotification(notification);
            return new Result<>(notification);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PutMapping(path = "{notificationID}/state/{state}")
    public Result<Notification> UpdateNotificationState(@PathVariable("notificationID") Long notificationID,
                                                        @PathVariable("state") String state) {
        try {
            Notification notification = notificationService.UpdateNotificationState(notificationID, state);
            return new Result<>(notification);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @DeleteMapping(path = "{notificationID}")
    public Result<Long> DeleteNotification(@PathVariable("notificationID") Long notificationID) {
        try {
            Long operationResult = notificationService.DeleteNotification(notificationID);
            return new Result<>(operationResult);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }
}
