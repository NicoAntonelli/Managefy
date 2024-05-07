package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import nicoAntonelli.managefy.repositories.UserRepository;
import nicoAntonelli.managefy.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository; // Dependency
    private final BusinessRepository businessRepository; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository,
                           UserRepository userRepository,
                           BusinessRepository businessRepository,
                           ErrorLogService errorLogService) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.errorLogService = errorLogService;
    }

    public List<UserRole> GetUserRoles() {
        try {
            return userRoleRepository.findAll();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Boolean ExistsUserRole(Long userID, Long businessID) {
        try {
            UserRoleKey userRoleKey = new UserRoleKey(userID, businessID);

            return userRoleRepository.existsById(userRoleKey);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public UserRole GetOneUserRole(Long userID, Long businessID) {
        try {
            UserRoleKey userRoleKey = new UserRoleKey(userID, businessID);

            Optional<UserRole> userRole = userRoleRepository.findById(userRoleKey);
            if (userRole.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneUserRole' - UserRole with ID: " + userRoleKey + " doesn't exist");
            }

            return userRole.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public UserRole CreateUserRole(UserRole userRole) {
        try {
            // Validate user
            Long userID = userRole.getUser().getId();
            if (!userRepository.existsById(userID)) {
                throw new IllegalStateException("Error at 'CreateUserRole' - User with ID: " + userID + " doesn't exist");
            }

            // Validate business
            Long businessID = userRole.getBusiness().getId();
            if (!businessRepository.existsById(businessID)) {
                throw new IllegalStateException("Error at 'CreateUserRole' - Business with ID: " + businessID + " doesn't exist");
            }

            return userRoleRepository.save(userRole);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public UserRole UpdateUserRole(UserRole userRole) {
        try {
            boolean exists = userRoleRepository.existsById(userRole.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateUserRole' - UserRole with ID: " + userRole.getId() + " doesn't exist");
            }

            return userRoleRepository.save(userRole);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public UserRoleKey DeleteUserRole(Long userRoleID, long businessID) {
        try {
            UserRoleKey userRoleKey = new UserRoleKey(userRoleID, businessID);
            boolean exists = ExistsUserRole(userRoleID, businessID);
            if (!exists) {
                throw new IllegalStateException("Error at 'DeleteUserRole' - User with ID: " + userRoleKey + " doesn't exist");
            }

            userRoleRepository.deleteById(userRoleKey);
            return userRoleKey;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
