package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.services.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/businesses")
public class BusinessController {
    private final BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping
    public List<Business> GetBusinesses() {
        return businessService.GetBusinesses();
    }

    @GetMapping(path = "{businessID}")
    public Business GetOneBusiness(@PathVariable("businessID") Long businessID) {
        return businessService.GetOneBusiness(businessID);
    }

    @PostMapping
    public Business CreateBusiness(@RequestBody Business business) {
        return businessService.CreateBusiness(business);
    }

    @PutMapping
    public Business UpdateBusiness(@RequestBody Business business) {
        return businessService.UpdateBusiness(business);
    }

    @DeleteMapping(path = "{businessID}")
    public Long BusinessIDUser(@PathVariable("businessID") Long businessID) {
        return businessService.DeleteBusiness(businessID);
    }
}
