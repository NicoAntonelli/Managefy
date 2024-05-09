package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.Login;
import nicoAntonelli.managefy.entities.dto.Registration;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.UserService;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
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
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{userID}")
    public Result<User> GetOneUser(@PathVariable("userID") Long userID) {
        try {
            User user = userService.GetOneUser(userID);
            return new Result<>(user);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping(path = "/register")
    public Result<String> Register(@RequestBody Registration registration) {
        try {
            String token = userService.CreateUser(registration);
            return new Result<>(token);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping(path = "/login")
    public Result<String> Login(@RequestBody Login login) {
        try {
            String token = userService.Login(login);
            return new Result<>(token);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping
    public Result<User> UpdateUser(@RequestBody User user) {
        try {
            user = userService.UpdateUser(user);
            return new Result<>(user);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{userID}")
    public Result<Long> DeleteUser(@PathVariable("userID") Long userID) {
        try {
            userID = userService.DeleteUser(userID);
            return new Result<>(userID);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}
