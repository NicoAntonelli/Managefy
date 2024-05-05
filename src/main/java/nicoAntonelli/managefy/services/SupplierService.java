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

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> GetSuppliers() {
        return supplierRepository.findAll();
    }

    public Boolean ExistsSupplier(Long supplierID) {
        return supplierRepository.existsById(supplierID);
    }

    public Supplier GetOneSupplier(Long supplierID) {
        Optional<Supplier> supplier = supplierRepository.findById(supplierID);
        if (supplier.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneSupplier' - Supplier with ID: " + supplierID + " doesn't exist");
        }

        return supplier.get();
    }

    public Supplier CreateSupplier(Supplier supplier) {
        supplier.setId(null);
        supplier.setDeletionDate(null);

        return supplierRepository.save(supplier);
    }

    public Supplier UpdateSupplier(Supplier supplier) {
        boolean exists = ExistsSupplier(supplier.getId());
        if (!exists) {
            throw new IllegalStateException("Error at 'UpdateSupplier' - Supplier with ID: " + supplier.getId() + " doesn't exist");
        }

        return supplierRepository.save(supplier);
    }

    // Logic deletion (field: deletion date)
    public Supplier DeleteSupplier(Long supplierID) {
        Supplier supplier = GetOneSupplier(supplierID);
        supplier.setDeletionDate(new Date());
        supplierRepository.save(supplier);

        return supplier;
    }
}
