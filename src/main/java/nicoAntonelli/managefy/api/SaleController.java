package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Sale;
import nicoAntonelli.managefy.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "api/sales")
public class SaleController {
    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<Sale> GetSales() {
        return saleService.GetSales();
    }

    @GetMapping(path = "interval")
    public List<Sale> GetSalesByInterval(@RequestParam Date from,
                                         @RequestParam Date to) {
        return saleService.GetSalesByInterval(from, to);
    }

    @GetMapping(path = "{saleID}")
    public Sale GetOneSale(@PathVariable("saleID") Long saleID) {
        return saleService.GetOneSale(saleID);
    }

    @PostMapping
    public Sale CreateSale(@RequestBody Sale sale) {
        return saleService.CreateSale(sale);
    }

    @PutMapping(path = "{saleID}/state/{state}")
    public Sale UpdateSaleState(@PathVariable("saleID") Long saleID,
                                @PathVariable("state") String state) {
        return saleService.UpdateSaleState(saleID, state);
    }

    @PutMapping(path = "{saleID}/partialPayment/{partialPayment}")
    public Sale UpdateSalePartialPayment(@PathVariable("saleID") Long saleID,
                                         @PathVariable("partialPayment") Float partialPayment) {
        return saleService.UpdateSalePartialPayment(saleID, partialPayment);
    }

    @DeleteMapping(path = "{saleID}")
    public Sale DeleteSale(@PathVariable("saleID") Long saleID) {
        return saleService.CancelSale(saleID);
    }
}
