package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.SupplierService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public SupplierController(SupplierService supplierService, ErrorLogService errorLogService) {
        this.supplierService = supplierService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Supplier>> GetSuppliers() {
        try {
            List<Supplier> suppliers = supplierService.GetSuppliers();
            return new Result<>(suppliers);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{supplierID}")
    public Result<Supplier> GetOneSupplier(@PathVariable("supplierID") Long supplierID) {
        try {
            Supplier supplier = supplierService.GetOneSupplier(supplierID);
            return new Result<>(supplier);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Supplier> CreateSupplier(@RequestBody Supplier supplier) {
        try {
            supplier = supplierService.CreateSupplier(supplier);
            return new Result<>(supplier);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping
    public Result<Supplier> UpdateSupplier(@RequestBody Supplier supplier) {
        try {
            supplier = supplierService.UpdateSupplier(supplier);
            return new Result<>(supplier);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{supplierID}")
    public Result<Supplier> DeleteSupplier(@PathVariable("supplierID") Long supplierID) {
        try {
            Supplier supplier = supplierService.DeleteSupplier(supplierID);
            return new Result<>(supplier);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}
