package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import nicoAntonelli.managefy.repositories.UserRepository;
import nicoAntonelli.managefy.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;
    private final UserService userService; // Dependency

    @Autowired
    public BusinessService(BusinessRepository businessRepository, UserService userService) {
        this.businessRepository = businessRepository;
        this.userService = userService;
    }

    public List<Business> GetBusinesses() {
        return businessRepository.findAll();
    }

    public Business GetOneBusiness(Long businessID) {
        Optional<Business> business = businessRepository.findById(businessID);
        if (business.isEmpty()) {
            throw new IllegalStateException("Business with ID: " + businessID + " doesn't exists");
        }

        return business.get();
    }

    public Business CreateBusiness(Business business) {
        return businessRepository.save(business);
    }

    public Business UpdateBusiness(Business business) {
        boolean exists = businessRepository.existsById(business.getId());
        if (!exists) {
            throw new IllegalStateException("Business with ID: " + business.getId() + " doesn't exists");
        }

        return businessRepository.save(business);
    }

    public Boolean AddUserRole(Long businessID, Long userID) {
        Business business = GetOneBusiness(businessID);
        User user = userService.GetOneUser(userID);

        business.addUser(user);
        businessRepository.save(business);

        return true;
    }

    public Long DeleteBusiness(Long businessID) {
        businessRepository.deleteById(businessID);
        return businessID;
    }
}
