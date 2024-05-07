package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.entities.dto.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/userRoles")
public class UserRoleController {
    private final UserRoleService userRoleService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public UserRoleController(UserRoleService userRoleService, ErrorLogService errorLogService) {
        this.userRoleService = userRoleService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<UserRole>> GetUserRoles() {
        try {
            List<UserRole> userRoles = userRoleService.GetUserRoles();
            return new Result<>(userRoles);
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

    @GetMapping(path = "{userID}/{businessID}")
    public Result<UserRole> GetOneUserRole(@PathVariable("userID") Long userID,
                                           @PathVariable("businessID") Long businessID) {
        try {
            UserRole userRole = userRoleService.GetOneUserRole(userID, businessID);
            return new Result<>(userRole);
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
    public Result<UserRole> CreateUserRole(@RequestBody UserRole userRole) {
        try {
            userRole = userRoleService.CreateUserRole(userRole);
            return new Result<>(userRole);
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

    @PutMapping
    public Result<UserRole> UpdateUserRole(@RequestBody UserRole userRole) {
        try {
            userRole = userRoleService.UpdateUserRole(userRole);
            return new Result<>(userRole);
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

    @DeleteMapping(path = "{userID}/{businessID}")
    public Result<UserRoleKey> UserRoleIDUser(@PathVariable("userID") Long userID,
                                              @PathVariable("businessID") Long businessID) {
        try {
            UserRoleKey userRoleKey = userRoleService.DeleteUserRole(userID, businessID);
            return new Result<>(userRoleKey);
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
