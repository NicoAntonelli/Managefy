package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.repositories.SupplierRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        return supplierRepository.findAllActives();
    }

    public Boolean ExistsSupplier(Long supplierID) {
        return supplierRepository.existsByIdActive(supplierID);
    }

    public Supplier GetOneSupplier(Long supplierID) {
        Optional<Supplier> supplier = supplierRepository.findByIdActive(supplierID);
        if (supplier.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneSupplier' - Supplier with ID: " + supplierID + " doesn't exist");
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
            throw new Exceptions.BadRequestException("Error at 'UpdateSupplier' - Supplier with ID: " + supplier.getId() + " doesn't exist");
        }

        return supplierRepository.save(supplier);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteSupplier(Long supplierID) {
        Supplier supplier = GetOneSupplier(supplierID);
        supplier.setDeletionDate(LocalDateTime.now());
        supplierRepository.save(supplier);

        return supplierID;
    }
}
