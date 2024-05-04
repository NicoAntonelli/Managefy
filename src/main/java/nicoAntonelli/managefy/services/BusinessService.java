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

    public Long DeleteBusiness(Long businessID) {
        businessRepository.deleteById(businessID);
        return businessID;
    }
}
