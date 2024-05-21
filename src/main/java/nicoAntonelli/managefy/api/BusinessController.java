package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.BusinessCU;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.BusinessService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.UserRoleService;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/businesses")
public class BusinessController {
    private final BusinessService businessService;
    private final UserRoleService userRoleService; // Dependency
    private final AuthService authService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public BusinessController(BusinessService businessService,
                              UserRoleService userRoleService,
                              AuthService authService,
                              ErrorLogService errorLogService) {
        this.businessService = businessService;
        this.userRoleService = userRoleService;
        this.authService = authService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public ResponseEntity<List<Business>> GetBusinesses(@RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetBusinesses");

            List<Business> businesses = businessService.GetBusinesses(user);
            return ResponseEntity.status(HttpStatus.OK).body(businesses);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "{businessID:[\\d]+}")
    public ResponseEntity<Business> GetOneBusiness(@PathVariable("businessID") Long businessID,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneBusiness");

            Business business = businessService.GetOneBusiness(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/link/{link:[A-Za-z0-9_-]+}")
    public ResponseEntity<Business> GetOneBusinessByLink(@PathVariable("link") String link,
                                                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneBusinessByLink");

            Business business = businessService.GetOneBusinessByLink(link, user);
            return ResponseEntity.status(HttpStatus.OK).body(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/linkPublic/{link:[A-Za-z0-9_-]+}")
    public ResponseEntity<Business> GetOneBusinessByLinkPublic(@PathVariable("link") String link) {
        try {
            Business business = businessService.GetOneBusinessByLinkPublic(link);
            return ResponseEntity.status(HttpStatus.OK).body(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Business> CreateBusiness(@RequestBody BusinessCU businessCU,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateBusiness");

            Business business = businessService.CreateBusiness(businessCU, user);

            // Set new manager role
            userRoleService.CreateUserRoleForNewBusiness(user.getId(), business.getId());

            return ResponseEntity.status(HttpStatus.OK).body(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Business> UpdateBusiness(@RequestBody BusinessCU businessCU,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateBusiness");

            Business business = businessService.UpdateBusiness(businessCU, user);
            return ResponseEntity.status(HttpStatus.OK).body(business);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{businessID:[\\d]+}")
    public ResponseEntity<Long> DeleteBusiness(@PathVariable("businessID") Long businessID,
                                               @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "DeleteBusiness");

            businessID = businessService.DeleteBusiness(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(businessID);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage(), ex.getStatus(), ex.getInnerException());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage(), Exceptions.InternalServerErrorException.status, ex.getCause());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
