package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> GetSuppliers() {
        return supplierService.GetSuppliers();
    }

    @GetMapping(path = "{supplierID}")
    public Supplier GetOneSupplier(@PathVariable("supplierID") Long supplierID) {
        return supplierService.GetOneSupplier(supplierID);
    }

    @PostMapping
    public Supplier CreateSupplier(@RequestBody Supplier supplier) {
        return supplierService.CreateSupplier(supplier);
    }

    @PutMapping
    public Supplier UpdateSupplier(@RequestBody Supplier supplier) {
        return supplierService.UpdateSupplier(supplier);
    }

    @DeleteMapping(path = "{supplierID}")
    public Supplier DeleteSupplier(@PathVariable("supplierID") Long supplierID) {
        return supplierService.DeleteSupplier(supplierID);
    }
}
