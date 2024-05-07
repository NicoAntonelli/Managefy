package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.dto.BusinessWithUser;
import nicoAntonelli.managefy.entities.dto.Result;
import nicoAntonelli.managefy.services.BusinessService;
import nicoAntonelli.managefy.services.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/businesses")
public class BusinessController {
    private final BusinessService businessService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public BusinessController(BusinessService businessService, ErrorLogService errorLogService) {
        this.businessService = businessService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Business>> GetBusinesses() {
        try {
            List<Business> businesses = businessService.GetBusinesses();
            return new Result<>(businesses);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @GetMapping(path = "{businessID}")
    public Result<Business> GetOneBusiness(@PathVariable("businessID") Long businessID) {
        try {
            Business business = businessService.GetOneBusiness(businessID);
            return new Result<>(business);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PostMapping
    public Result<Business> CreateBusiness(@RequestBody BusinessWithUser businessWithUser) {
        try {
            Business business = businessService.CreateBusiness(businessWithUser);
            return new Result<>(business);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PutMapping
    public Result<Business> UpdateBusiness(@RequestBody Business business) {
        try {
            business = businessService.UpdateBusiness(business);
            return new Result<>(business);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @DeleteMapping(path = "{businessID}")
    public Result<Long> BusinessIDUser(@PathVariable("businessID") Long businessID) {
        try {
            businessID = businessService.DeleteBusiness(businessID);
            return new Result<>(businessID);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }
}
