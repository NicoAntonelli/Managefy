package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Sale;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.SaleC;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.SaleService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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
    public Result<List<Sale>> GetSalesIncomplete(@PathVariable("businessID") Long businessID,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesIncomplete");

            List<Sale> sales = saleService.GetSalesIncomplete(businessID, user);
            return new Result<>(sales);
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

    @GetMapping(path = "business/{businessID:[\\d]+}/client/{clientID:[\\d]+}")
    public Result<List<Sale>> GetSalesByClient(@PathVariable("businessID") Long businessID,
                                               @PathVariable("clientID") Long clientID,
                                               @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesByClient");

            List<Sale> sales = saleService.GetSalesByClient(businessID, clientID, user);
            return new Result<>(sales);
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

    @GetMapping(path = "business/{businessID:[\\d]+}/interval")
    public Result<List<Sale>> GetSalesByInterval(@PathVariable("businessID") Long businessID,
                                                 @RequestParam String from,
                                                 @RequestParam String to,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetSalesByInterval");

            List<Sale> sales = saleService.GetSalesByInterval(businessID, from, to, user);
            return new Result<>(sales);
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

    @GetMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}")
    public Result<Sale> GetOneSale(@PathVariable("saleID") Long saleID,
                                   @PathVariable("businessID") Long businessID,
                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "GetOneSale");

            Sale sale = saleService.GetOneSale(saleID, businessID, user);
            return new Result<>(sale);
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
    public Result<Sale> CreateSale(@RequestBody SaleC saleC,
                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CreateSale");

            Sale sale = saleService.CreateSale(saleC, user);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/state/{state:[a-zA-Z]+}")
    public Result<Sale> UpdateSaleState(@PathVariable("saleID") Long saleID,
                                        @PathVariable("businessID") Long businessID,
                                        @PathVariable("state") String state,
                                        @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateSaleState");

            Sale sale = saleService.UpdateSaleState(saleID, businessID, state, user);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}/partialPayment/{partialPayment:(?:[0-9]*[.])?[0-9]+}")
    public Result<Sale> UpdateSalePartialPayment(@PathVariable("saleID") Long saleID,
                                                 @PathVariable("businessID") Long businessID,
                                                 @PathVariable("partialPayment") BigDecimal partialPayment,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateSalePartialPayment");

            Sale sale = saleService.UpdateSalePartialPayment(saleID, businessID, partialPayment, user);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/client/{clientID:[\\d]+}")
    public Result<Sale> UpdateOrAddClientForSale(@PathVariable("saleID") Long saleID,
                                                 @PathVariable("businessID") Long businessID,
                                                 @PathVariable("clientID") Long clientID,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "UpdateOrAddClientForSale");

            Sale sale = saleService.UpdateOrAddClientForSale(saleID, businessID, clientID, user);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID}/business/{businessID:[\\d]+}/eraseClient")
    public Result<Sale> EraseClientForSale(@PathVariable("saleID") Long saleID,
                                           @PathVariable("businessID") Long businessID,
                                           @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "EraseClientForSale");

            Sale sale = saleService.EraseClientForSale(saleID, businessID, user);
            return new Result<>(sale);
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

    @DeleteMapping(path = "{saleID:[\\d]+}/business/{businessID:[\\d]+}")
    public Result<Long> CancelSale(@PathVariable("saleID") Long saleID,
                                   @PathVariable("businessID") Long businessID,
                                   @RequestHeader HttpHeaders headers) {
        try {
            User user = authService.validateTokenFromHeaders(headers, "CancelSale");

            saleID = saleService.CancelSale(saleID, businessID, user);
            return new Result<>(saleID);
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
