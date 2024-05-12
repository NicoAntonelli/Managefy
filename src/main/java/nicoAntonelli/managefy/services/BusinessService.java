package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserRoleService userRoleService; // Dependency

    @Autowired
    public BusinessService(BusinessRepository businessRepository, UserRoleService userRoleService) {
        this.businessRepository = businessRepository;
        this.userRoleService = userRoleService;
    }

    public List<Business> GetBusinesses(User user) {
        return businessRepository.findByUser(user.getId());
    }

    public Boolean ExistsBusiness(Long businessID, User user, String minimumRole) {
        return switch (minimumRole) {
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

    public Business GetOneBusinessByLink(String link, User user) {
        Optional<Business> business = businessRepository.findByLink(link, user.getId());
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusinessByLink' - Business with Link: " + link + " doesn't exist or the user: " + user.getId() + " don't have a rol in it");
        }

        return business.get();
    }

    public Business GetOneBusinessByLinkPublic(String link) {
        Optional<Business> business = businessRepository.findByLinkPublic(link);
        if (business.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneBusinessByLinkPublic' - Business with Link: " + link + " doesn't exist or the business it's not public");
        }

        return business.get();
    }

    public Business CreateBusiness(Business business, User user) {
        // Validate simple attributes
        if (business.getName() == null || business.getDescription() == null || business.getLink() == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateBusiness' - One or more of the required fields were not supplied");
        }

        // Link unique validation
        Optional<Business> possibleBusiness = businessRepository.findByLink(business.getLink(), user.getId());
        if (possibleBusiness.isPresent()) {
            throw new Exceptions.BadRequestException("Error at 'CreateBusiness' - Link '" + business.getLink() + "' already taken");
        }

        // Validate business days (Optional: has a default value if not supplied)
        ValidateBusinessDays(business);

        // Forced initial state
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

        // Validate simple attributes
        if (business.getName() == null || business.getDescription() == null || business.getLink() == null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - One or more of the required fields were not supplied");
        }

        // Link unique validation
        Optional<Business> possibleBusiness = businessRepository.findByLink(business.getLink(), user.getId());
        if (possibleBusiness.isPresent()) {
            // Only fail validation if it's not the same business
            if (!Objects.equals(possibleBusiness.get().getId(), business.getId())) {
                throw new Exceptions.BadRequestException("Error at 'UpdateBusiness' - Link '" + business.getLink() + "' already taken");
            }
        }

        // Validate business days (Optional: has a default value if not supplied)
        ValidateBusinessDays(business);

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

    private void ValidateBusinessDays(Business business) {
        SortedMap<String, Boolean> businessDays = business.getBusinessDays();
        if (business.getBusinessDays() != null) {
            if (businessDays.size() != 7) {
                throw new Exceptions.BadRequestException("Error at 'ValidateBusinessDays' - Business days need to have a total of 7 days");
            }

            List<String> weekDays = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
            for (String key : businessDays.keySet()) {
                if (!weekDays.contains(key)) {
                    throw new Exceptions.BadRequestException("Error at 'ValidateBusinessDays' - Business days don't contain '" + key + "'");
                }
            }
        }
    }
}
