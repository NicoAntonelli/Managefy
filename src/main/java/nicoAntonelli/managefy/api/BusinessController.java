package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.BusinessService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/businesses")
public class BusinessController {
    private final BusinessService businessService;
    private final AuthService authService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public BusinessController(BusinessService businessService,
                              AuthService authService,
                              ErrorLogService errorLogService) {
        this.businessService = businessService;
        this.authService = authService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Business>> GetBusinesses(@RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetBusinesses");

            List<Business> businesses = businessService.GetBusinesses(user);
            return new Result<>(businesses);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{businessID:[\\d]+}")
    public Result<Business> GetOneBusiness(@PathVariable("businessID") Long businessID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneBusiness");

            Business business = businessService.GetOneBusiness(businessID, user);
            return new Result<>(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Business> CreateBusiness(@RequestBody Business business,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateBusiness");

            business = businessService.CreateBusiness(business, user);
            return new Result<>(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping
    public Result<Business> UpdateBusiness(@RequestBody Business business,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateBusiness");

            business = businessService.UpdateBusiness(business, user);
            return new Result<>(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{businessID:[\\d]+}")
    public Result<Long> DeleteBusiness(@PathVariable("businessID") Long businessID,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "DeleteBusiness");

            businessID = businessService.DeleteBusiness(businessID, user);
            return new Result<>(businessID);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}
