package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/users")
public class UserController {
    private final UserService userService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public UserController(UserService userService, ErrorLogService errorLogService) {
        this.userService = userService;
        this.errorLogService = errorLogService;
    }

    @GetMapping()
    public Result<List<User>> GetUsers() {
        try {
            List<User> users = userService.GetUsers();
            return new Result<>(users);
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

    @GetMapping(path = "{userID}")
    public Result<User> GetOneUser(@PathVariable("userID") Long userID) {
        try {
            User user = userService.GetOneUser(userID);
            return new Result<>(user);
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

    @PostMapping(path = "/register")
    public Result<User> Register(@RequestBody User user) {
        try {
            user = userService.CreateUser(user);
            return new Result<>(user);
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

    @PostMapping(path = "/login")
    public Result<User> Login(@RequestBody User user) {
        try {
            user = userService.ValidateUser(user.getMail(), user.getPassword());
            return new Result<>(user);
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
    public Result<User> UpdateUser(@RequestBody User user) {
        try {
            user = userService.UpdateUser(user);
            return new Result<>(user);
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

    @DeleteMapping(path = "{userID}")
    public Result<Long> DeleteUser(@PathVariable("userID") Long userID) {
        try {
            userID = userService.DeleteUser(userID);
            return new Result<>(userID);
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
