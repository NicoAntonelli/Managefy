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

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           UserService userService,
                           UserRoleService userRoleService) {
        this.businessRepository = businessRepository;
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    public List<Business> GetBusinesses() {
        return businessRepository.findAll();
    }

    public Boolean ExistsBusiness(Long businessID) {
        return businessRepository.existsById(businessID);
    }

    public Business GetOneBusiness(Long businessID) {
        Optional<Business> business = businessRepository.findById(businessID);
        if (business.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneBusiness' - Business with ID: " + businessID + " doesn't exists");
        }

        return business.get();
    }

    public Business CreateBusiness(BusinessWithUser businessWithUser) {
        // Validate associated user ID
        boolean exists = userService.ExistsUser(businessWithUser.getUserID());
        if (!exists) {
            throw new IllegalStateException("Error at 'CreateBusiness' - User with ID: " + businessWithUser.getUserID() + " doesn't exists");
        }

        // Create business
        Business business = businessWithUser.getBusiness();
        business.setId(null);
        business = businessRepository.save(business);

        // Set associated user
        User user = new User();
        user.setId(businessWithUser.getUserID());

        // Create user role
        UserRole userRole = new UserRole(user, business, true, false, false);
        userRoleService.CreateUserRole(userRole);

        return business;
    }

    public Business UpdateBusiness(Business business) {
        boolean exists = ExistsBusiness(business.getId());
        if (!exists) {
            throw new IllegalStateException("Error at 'UpdateBusiness' - Business with ID: " + business.getId() + " doesn't exists");
        }

        return businessRepository.save(business);
    }

    public Long DeleteBusiness(Long businessID) {
        boolean exists = ExistsBusiness(businessID);
        if (!exists) {
            throw new IllegalStateException("Error at 'DeleteBusiness' - Business with ID: " + businessID + " doesn't exists");
        }

        businessRepository.deleteById(businessID);
        return businessID;
    }
}
