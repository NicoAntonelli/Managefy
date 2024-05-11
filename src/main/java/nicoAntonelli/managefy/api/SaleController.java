package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Sale;
import nicoAntonelli.managefy.services.AuthService;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.SaleService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public Result<List<Sale>> GetSales(@RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetSales");

            List<Sale> sales = saleService.GetSales();
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

    @GetMapping(path = "interval")
    public Result<List<Sale>> GetSalesByInterval(@RequestParam String from,
                                                 @RequestParam String to,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetSalesByInterval");

            List<Sale> sales = saleService.GetSalesByInterval(from, to);
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

    @GetMapping(path = "{saleID:[\\d]+}")
    public Result<Sale> GetOneSale(@PathVariable("saleID") Long saleID,
                                   @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "GetOneSale");

            Sale sale = saleService.GetOneSale(saleID);
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
    public Result<Sale> CreateSale(@RequestBody Sale sale,
                                   @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "CreateSale");

            sale = saleService.CreateSale(sale);
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

    @PutMapping(path = "{saleID}/state/{state:[a-zA-Z]+}")
    public Result<Sale> UpdateSaleState(@PathVariable("saleID") Long saleID,
                                        @PathVariable("state") String state,
                                        @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "UpdateSaleState");

            Sale sale = saleService.UpdateSaleState(saleID, state);
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

    @PutMapping(path = "{saleID:[\\d]+}/partialPayment/{partialPayment:(?:[0-9]*[.])?[0-9]+}")
    public Result<Sale> UpdateSalePartialPayment(@PathVariable("saleID") Long saleID,
                                                 @PathVariable("partialPayment") Float partialPayment,
                                                 @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "UpdateSalePartialPayment");

            Sale sale = saleService.UpdateSalePartialPayment(saleID, partialPayment);
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

    @DeleteMapping(path = "{saleID:[\\d]+}")
    public Result<Long> CancelSale(@PathVariable("saleID") Long saleID,
                                   @RequestHeader HttpHeaders headers) {
        try {
            authService.validateTokenFromHeaders(headers, "CancelSale");

            saleID = saleService.CancelSale(saleID);
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
