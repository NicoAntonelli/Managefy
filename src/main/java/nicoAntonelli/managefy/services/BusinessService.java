package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BusinessService {
    private final BusinessRepository businessRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
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

    public Business CreateBusiness(Business business) {
        business.setId(null);
        return businessRepository.save(business);
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
