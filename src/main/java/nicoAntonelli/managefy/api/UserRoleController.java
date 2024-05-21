package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.UserRole;
import nicoAntonelli.managefy.entities.UserRoleKey;
import nicoAntonelli.managefy.services.AuthService;
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
@RequestMapping(path = "api/userRoles")
public class UserRoleController {
    private final UserRoleService userRoleService;
    private final AuthService authService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public UserRoleController(UserRoleService userRoleService,
                              AuthService authService,
                              ErrorLogService errorLogService) {
        this.userRoleService = userRoleService;
        this.authService = authService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public ResponseEntity<List<UserRole>> GetUserRoles(@RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetUserRoles");

            List<UserRole> userRoles = userRoleService.GetUserRoles(user);
            return ResponseEntity.status(HttpStatus.OK).body(userRoles);
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

    @GetMapping(path = "business/{businessID:[\\d]+}")
    public ResponseEntity<List<UserRole>> GetUserRolesByBusiness(@PathVariable("businessID") Long businessID,
                                                                 @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetUserRoles");

            List<UserRole> userRoles = userRoleService.GetUserRolesByBusiness(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRoles);
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

    @GetMapping(path = "loggedRole/business/{businessID:[\\d]+}")
    public ResponseEntity<UserRole> GetOneUserRoleForLogged(@PathVariable("businessID") Long businessID,
                                                            @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneUserRole");

            UserRole userRole = userRoleService.GetOneUserRoleForLogged(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRole);
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

    @GetMapping(path = "user/{otherUserID:[\\d]+}/business/{businessID:[\\d]+}")
    public ResponseEntity<UserRole> GetOneUserRoleForOther(@PathVariable("otherUserID") Long otherUserID,
                                                           @PathVariable("businessID") Long businessID,
                                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneUserRole");

            UserRole userRole = userRoleService.GetOneUserRoleForOther(otherUserID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRole);
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

    @PutMapping(path = "user/{otherUserID:[\\d]+}/business/{businessID:[\\d]+}/createRole/{role:[a-zA-Z]+}")
    public ResponseEntity<UserRole> CreateUserRole(@PathVariable("otherUserID") Long otherUserID,
                                                   @PathVariable("businessID") Long businessID,
                                                   @PathVariable("role") String role,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateUserRole");

            UserRole userRole = userRoleService.CreateUserRole(otherUserID, businessID, role, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRole);
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

    @PutMapping(path = "user/{otherUserID:[\\d]+}/business/{businessID:[\\d]+}/updateRole/{role:[a-zA-Z]+}")
    public ResponseEntity<UserRole> UpdateUserRole(@PathVariable("otherUserID") Long otherUserID,
                                                   @PathVariable("businessID") Long businessID,
                                                   @PathVariable("role") String role,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateUserRole");

            UserRole userRole = userRoleService.UpdateUserRole(otherUserID, businessID, role, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRole);
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

    @PutMapping(path = "user/{otherUserID:[\\d]+}/business/{businessID:[\\d]+}/transferManager")
    public ResponseEntity<UserRole> TransferManagerRole(@PathVariable("otherUserID") Long otherUserID,
                                                        @PathVariable("businessID") Long businessID,
                                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "TransferManagerRole");

            UserRole userRole = userRoleService.TransferManagerRole(otherUserID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRole);
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

    @DeleteMapping(path = "user/{otherUserID:[\\d]+}/business/{businessID:[\\d]+}")
    public ResponseEntity<UserRoleKey> DeleteUserRole(@PathVariable("otherUserID") Long otherUserID,
                                                      @PathVariable("businessID") Long businessID,
                                                      @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "DeleteUserRole");

            UserRoleKey userRoleKey = userRoleService.DeleteUserRole(otherUserID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRoleKey);
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

    @DeleteMapping(path = "/business/{businessID:[\\d]+}")
    public ResponseEntity<UserRoleKey> LeaveUserRole(@PathVariable("businessID") Long businessID,
                                                     @RequestHeader HttpHeaders headers){
        try {
            User user = authService.validateTokenFromHeaders(headers, "LeaveUserRole");

            UserRoleKey userRoleKey = userRoleService.LeaveUserRole(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(userRoleKey);
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
