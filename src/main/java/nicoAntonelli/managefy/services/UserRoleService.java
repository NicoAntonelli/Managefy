package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserService userService; // Dependency
    private final BusinessService businessService; // Dependency

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository,
                           UserService userService,
                           BusinessService businessService) {
        this.userRoleRepository = userRoleRepository;
        this.userService = userService;
        this.businessService = businessService;
    }

    public List<UserRole> GetUserRoles() {
        return userRoleRepository.findAll();
    }

    public UserRole GetOneUserRole(Long userID, long businessID) {
        UserRoleKey userRoleKey = new UserRoleKey(userID, businessID);

        Optional<UserRole> userRole = userRoleRepository.findById(userRoleKey);
        if (userRole.isEmpty()) {
            throw new IllegalStateException("UserRole with ID: " + userRoleKey + " doesn't exists");
        }

        return userRole.get();
    }

    public UserRole CreateUserRole(UserRole userRole) {
        // This 'Get one' methods throws error if entity doesn't exist
        userService.GetOneUser(userRole.getUser().getId());
        businessService.GetOneBusiness(userRole.getBusiness().getId());

        return userRoleRepository.save(userRole);
    }

    public UserRole UpdateUserRole(UserRole userRole) {
        boolean exists = userRoleRepository.existsById(userRole.getId());
        if (!exists) {
            throw new IllegalStateException("UserRole with ID: " + userRole.getId() + " doesn't exists");
        }

        return userRoleRepository.save(userRole);
    }

    public UserRoleKey DeleteUserRole(Long userRoleID, long businessID) {
        UserRoleKey userRoleKey = new UserRoleKey(userRoleID, businessID);

        userRoleRepository.deleteById(userRoleKey);
        return userRoleKey;
    }
}
