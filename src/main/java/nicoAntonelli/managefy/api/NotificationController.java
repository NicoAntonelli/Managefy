package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Notification;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.NotificationService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AuthService authService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  AuthService authService,
                                  ErrorLogService errorLogService) {
        this.notificationService = notificationService;
        this.authService = authService;
        this.errorLogService = errorLogService;
    }

    @GetMapping(path = "/user/{userID:[\\d]+}")
    public Result<List<Notification>> GetNotificationsByUser(@PathVariable("userID") Long userID,
                                                             @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetNotificationsByUser");

            List<Notification> notifications = notificationService.GetNotificationsByUser(userID);
            return new Result<>(notifications);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{notificationID:[\\d]+}")
    public Result<Notification> GetOneNotification(@PathVariable("notificationID") Long notificationID,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetOneNotification");

            Notification notification = notificationService.GetOneNotification(notificationID);
            return new Result<>(notification);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Notification> CreateNotification(@RequestBody Notification notification,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "CreateNotification");

            notification = notificationService.CreateNotification(notification);
            return new Result<>(notification);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping(path = "{notificationID:[\\d]+}/state/{state:[a-zA-Z]+}")
    public Result<Notification> UpdateNotificationState(@PathVariable("notificationID") Long notificationID,
                                                        @PathVariable("state") String state,
                                                        @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "UpdateNotificationState");

            Notification notification = notificationService.UpdateNotificationState(notificationID, state);
            return new Result<>(notification);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{notificationID:[\\d]+}")
    public Result<Long> DeleteNotification(@PathVariable("notificationID") Long notificationID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "DeleteNotification");

            notificationID = notificationService.DeleteNotification(notificationID);
            return new Result<>(notificationID);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}
