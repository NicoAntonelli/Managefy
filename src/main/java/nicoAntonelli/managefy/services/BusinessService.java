package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.dto.BusinessWithUser;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserService userService; // Dependency
    private final UserRoleService userRoleService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           UserService userService,
                           UserRoleService userRoleService,
                           ErrorLogService errorLogService) {
        this.businessRepository = businessRepository;
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.errorLogService = errorLogService;
    }

    public List<Business> GetBusinesses() {
        try {
            return businessRepository.findAll();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Boolean ExistsBusiness(Long businessID) {
        try {
            return businessRepository.existsById(businessID);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Business GetOneBusiness(Long businessID) {
        try {
            Optional<Business> business = businessRepository.findById(businessID);
            if (business.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneBusiness' - Business with ID: " + businessID + " doesn't exist");
            }

            return business.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Business CreateBusiness(BusinessWithUser businessWithUser) {
        try {
            // Validate associated user ID
            boolean exists = userService.ExistsUser(businessWithUser.getUserID());
            if (!exists) {
                throw new IllegalStateException("Error at 'CreateBusiness' - User with ID: " + businessWithUser.getUserID() + " doesn't exist");
            }

            // Create business
            Business business = businessWithUser.getBusiness();
            business.setId(null);
            business = businessRepository.save(business);

            // Set associated user
            User user = new User(businessWithUser.getUserID());

            // Create user role
            UserRole userRole = new UserRole(user, business, true, false, false);
            userRoleService.CreateUserRole(userRole);

            return business;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Business UpdateBusiness(Business business) {
        try {
            boolean exists = ExistsBusiness(business.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateBusiness' - Business with ID: " + business.getId() + " doesn't exist");
            }

            return businessRepository.save(business);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Long DeleteBusiness(Long businessID) {
        try {
            boolean exists = ExistsBusiness(businessID);
            if (!exists) {
                throw new IllegalStateException("Error at 'DeleteBusiness' - Business with ID: " + businessID + " doesn't exist");
            }

            businessRepository.deleteById(businessID);
            return businessID;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
