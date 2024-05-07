package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Sale;
import nicoAntonelli.managefy.entities.dto.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "api/sales")
public class SaleController {
    private final SaleService saleService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public SaleController(SaleService saleService, ErrorLogService errorLogService) {
        this.saleService = saleService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Sale>> GetSales() {
        try {
            List<Sale> sales = saleService.GetSales();
            return new Result<>(sales);
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

    @GetMapping(path = "interval")
    public Result<List<Sale>> GetSalesByInterval(@RequestParam Date from,
                                                 @RequestParam Date to) {
        try {
            List<Sale> sales = saleService.GetSalesByInterval(from, to);
            return new Result<>(sales);
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

    @GetMapping(path = "{saleID}")
    public Result<Sale> GetOneSale(@PathVariable("saleID") Long saleID) {
        try {
            Sale sale = saleService.GetOneSale(saleID);
            return new Result<>(sale);
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
    public Result<Sale> CreateSale(@RequestBody Sale sale) {
        try {
            sale = saleService.CreateSale(sale);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID}/state/{state}")
    public Result<Sale> UpdateSaleState(@PathVariable("saleID") Long saleID,
                                        @PathVariable("state") String state) {
        try {
            Sale sale = saleService.UpdateSaleState(saleID, state);
            return new Result<>(sale);
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

    @PutMapping(path = "{saleID}/partialPayment/{partialPayment}")
    public Result<Sale> UpdateSalePartialPayment(@PathVariable("saleID") Long saleID,
                                                 @PathVariable("partialPayment") Float partialPayment) {
        try {
            Sale sale = saleService.UpdateSalePartialPayment(saleID, partialPayment);
            return new Result<>(sale);
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

    @DeleteMapping(path = "{saleID}")
    public Result<Sale> CancelSale(@PathVariable("saleID") Long saleID) {
        try {
            Sale sale = saleService.CancelSale(saleID);
            return new Result<>(sale);
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
