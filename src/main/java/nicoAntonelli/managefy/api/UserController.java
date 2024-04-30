package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> GetUsers() {
        return userService.GetUsers();
    }

    @GetMapping(path = "{userID}")
    public User GetOneUser(@PathVariable("userID") Long userID) {
        return userService.GetOneUser(userID);
    }

    @PostMapping(path = "/register")
    public User Register(@RequestBody User user) {
        user.setId(null);
        return userService.CreateUser(user);
    }

    @PostMapping(path = "/login")
    public User Login(@RequestBody User user) {
        return userService.ValidateUser(user.getMail(), user.getPassword());
    }

    @PutMapping
    public User UpdateUser(@RequestBody User user) {
        return userService.UpdateUser(user);
    }

    @DeleteMapping(path = "{userID}")
    public Long DeleteUser(@PathVariable("userID") Long userID) {
        return userService.DeleteUser(userID);
    }
}
