package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.dto.BusinessWithUser;
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
            authService.validateTokenFromHeaders(headers, "GetBusinesses");

            List<Business> businesses = businessService.GetBusinesses();
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

    @GetMapping(path = "{businessID}")
    public Result<Business> GetOneBusiness(@PathVariable("businessID") Long businessID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetOneBusiness");

            Business business = businessService.GetOneBusiness(businessID);
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
    public Result<Business> CreateBusiness(@RequestBody BusinessWithUser businessWithUser,
                                           @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "CreateBusiness");

            Business business = businessService.CreateBusiness(businessWithUser);
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
            authService.validateTokenFromHeaders(headers, "UpdateBusiness");

            business = businessService.UpdateBusiness(business);
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

    @DeleteMapping(path = "{businessID}")
    public Result<Long> DeleteBusiness(@PathVariable("businessID") Long businessID,
                                       @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "DeleteBusiness");

            businessID = businessService.DeleteBusiness(businessID);
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
