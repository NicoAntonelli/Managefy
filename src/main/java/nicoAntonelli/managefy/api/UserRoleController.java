package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.services.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/userRoles")
public class UserRoleController {
    private final UserRoleService userRoleService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public List<UserRole> GetUserRoles() {
        return userRoleService.GetUserRoles();
    }

    @GetMapping(path = "{userID}/{businessID}")
    public UserRole GetOneUserRole(@PathVariable("userID") Long userID,
                                   @PathVariable("businessID") Long businessID) {
        return userRoleService.GetOneUserRole(userID, businessID);
    }

    @PostMapping
    public UserRole CreateUserRole(@RequestBody UserRole userRole) {
        return userRoleService.CreateUserRole(userRole);
    }

    @PutMapping
    public UserRole UpdateUserRole(@RequestBody UserRole userRole) {
        return userRoleService.UpdateUserRole(userRole);
    }

    @DeleteMapping(path = "{userID}/{businessID}")
    public UserRoleKey UserRoleIDUser(@PathVariable("userID") Long userID,
                                      @PathVariable("businessID") Long businessID) {
        return userRoleService.DeleteUserRole(userID, businessID);
    }
}
