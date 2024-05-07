package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, ErrorLogService errorLogService) {
        this.supplierRepository = supplierRepository;
        this.errorLogService = errorLogService;
    }

    public List<Supplier> GetSuppliers() {
        try {
            return supplierRepository.findAll();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Boolean ExistsSupplier(Long supplierID) {
        try {
            return supplierRepository.existsById(supplierID);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Supplier GetOneSupplier(Long supplierID) {
        try {
            Optional<Supplier> supplier = supplierRepository.findById(supplierID);
            if (supplier.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneSupplier' - Supplier with ID: " + supplierID + " doesn't exist");
            }

            return supplier.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Supplier CreateSupplier(Supplier supplier) {
        try {
            supplier.setId(null);
            supplier.setDeletionDate(null);

            return supplierRepository.save(supplier);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Supplier UpdateSupplier(Supplier supplier) {
        try {
            boolean exists = ExistsSupplier(supplier.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateSupplier' - Supplier with ID: " + supplier.getId() + " doesn't exist");
            }

            return supplierRepository.save(supplier);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    // Logic deletion (field: deletion date)
    public Supplier DeleteSupplier(Long supplierID) {
        try {
            Supplier supplier = GetOneSupplier(supplierID);
            supplier.setDeletionDate(new Date());
            supplierRepository.save(supplier);

            return supplier;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
