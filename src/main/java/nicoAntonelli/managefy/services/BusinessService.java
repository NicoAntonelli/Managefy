package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.dto.BusinessWithUser;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import nicoAntonelli.managefy.utils.Exceptions;
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

    public List<Business> GetBusinesses(User user) {
        return businessRepository.findByUser(user.getId());
    }

    public Boolean ExistsBusiness(Long businessID, User user, String role) {
        return switch (role) {
            case "admin" -> businessRepository.existsByIdAndUserAdmin(businessID, user.getId());
            case "manager" -> businessRepository.existsByIdAndUserManager(businessID, user.getId());
            default -> businessRepository.existsByIdAndUser(businessID, user.getId());
        };
    }

    public Business GetOneBusiness(Long businessID, User user) {
        Optional<Business> business = businessRepository.findByIdAndUser(businessID, user.getId());
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusiness' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " don't have a rol in it");
        }

        return business.get();
    }

    public Business CreateBusiness(Business business, User user) {
        // Validate associated user ID
        boolean exists = userService.ExistsUser(user.getId());
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'CreateBusiness' - User with ID: " + user.getId() + " doesn't exist");
        }

        // Create business
        business.setId(null);
        business = businessRepository.save(business);

        // Create user role (Manager)
        UserRole userRole = new UserRole(user, business, true, false, false);
        userRoleService.CreateUserRole(userRole);

        return business;
    }

    public Business UpdateBusiness(Business business, User user) {
        boolean exists = ExistsBusiness(business.getId(), user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - Business with ID: " + business.getId() + " doesn't exist or the user: " + user.getId() + " isn't an Admin");
        }

        return businessRepository.save(business);
    }

    public Long DeleteBusiness(Long businessID, User user) {
        boolean exists = ExistsBusiness(businessID, user, "manager");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteBusiness' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't the Manager");
        }

        businessRepository.deleteById(businessID);
        return businessID;
    }
}
