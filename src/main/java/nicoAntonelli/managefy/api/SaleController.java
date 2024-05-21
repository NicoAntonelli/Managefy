package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Sale;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.SaleC;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.SaleService;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/sales")
public class SaleController {
    private final SaleService saleService;
    private final AuthService authService; // Dependency
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public SaleController(SaleService saleService,
                          AuthService authService,
                          ErrorLogService errorLogService) {
        this.saleService = saleService;
        this.authService = authService;
        this.errorLogService = errorLogService;
    }

    @GetMapping(path = "business/{businessID:[\\d]+}")
    public ResponseEntity<List<Sale>> GetSalesIncomplete(@PathVariable("businessID") Long businessID,
                                                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesIncomplete");

            List<Sale> sales = saleService.GetSalesIncomplete(businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(sales);
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

    @GetMapping(path = "business/{businessID:[\\d]+}/client/{clientID:[\\d]+}")
    public ResponseEntity<List<Sale>> GetSalesByClient(@PathVariable("businessID") Long businessID,
                                                       @PathVariable("clientID") Long clientID,
                                                       @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesByClient");

            List<Sale> sales = saleService.GetSalesByClient(businessID, clientID, user);
            return ResponseEntity.status(HttpStatus.OK).body(sales);
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

    @GetMapping(path = "business/{businessID:[\\d]+}/interval")
    public ResponseEntity<List<Sale>> GetSalesByInterval(@PathVariable("businessID") Long businessID,
                                                         @RequestParam String from,
                                                         @RequestParam String to,
                                                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesByInterval");

            List<Sale> sales = saleService.GetSalesByInterval(businessID, from, to, user);
            return ResponseEntity.status(HttpStatus.OK).body(sales);
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

    @GetMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}")
    public ResponseEntity<Sale> GetOneSale(@PathVariable("saleID") Long saleID,
                                           @PathVariable("businessID") Long businessID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneSale");

            Sale sale = saleService.GetOneSale(saleID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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
    public ResponseEntity<Sale> CreateSale(@RequestBody SaleC saleC,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateSale");

            Sale sale = saleService.CreateSale(saleC, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/observation/{observation}")
    public ResponseEntity<Sale> UpdateSaleObservation(@PathVariable("saleID") Long saleID,
                                                      @PathVariable("businessID") Long businessID,
                                                      @PathVariable("observation") String observation,
                                                      @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateSaleObservation");

            Sale sale = saleService.UpdateSaleObservation(saleID, businessID, observation, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/state/{state:[a-zA-Z]+}")
    public ResponseEntity<Sale> UpdateSaleState(@PathVariable("saleID") Long saleID,
                                                @PathVariable("businessID") Long businessID,
                                                @PathVariable("state") String state,
                                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateSaleState");

            Sale sale = saleService.UpdateSaleState(saleID, businessID, state, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @PutMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}/partialPayment/{partialPayment:(?:[0-9]*[.])?[0-9]+}")
    public ResponseEntity<Sale> UpdateSalePartialPayment(@PathVariable("saleID") Long saleID,
                                                         @PathVariable("businessID") Long businessID,
                                                         @PathVariable("partialPayment") BigDecimal partialPayment,
                                                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateSalePartialPayment");

            Sale sale = saleService.UpdateSalePartialPayment(saleID, businessID, partialPayment, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/client/{clientID:[\\d]+}")
    public ResponseEntity<Sale> UpdateOrAddClientForSale(@PathVariable("saleID") Long saleID,
                                                         @PathVariable("businessID") Long businessID,
                                                         @PathVariable("clientID") Long clientID,
                                                         @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateOrAddClientForSale");

            Sale sale = saleService.UpdateOrAddClientForSale(saleID, businessID, clientID, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/eraseClient")
    public ResponseEntity<Sale> EraseClientForSale(@PathVariable("saleID") Long saleID,
                                                   @PathVariable("businessID") Long businessID,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "EraseClientForSale");

            Sale sale = saleService.EraseClientForSale(saleID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(sale);
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

    @DeleteMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}")
    public ResponseEntity<Long> CancelSale(@PathVariable("saleID") Long saleID,
                                           @PathVariable("businessID") Long businessID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CancelSale");

            saleID = saleService.CancelSale(saleID, businessID, user);
            return ResponseEntity.status(HttpStatus.OK).body(saleID);
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
