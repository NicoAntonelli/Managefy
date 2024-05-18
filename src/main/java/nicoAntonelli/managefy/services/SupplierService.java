package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.SupplierCU;
import nicoAntonelli.managefy.repositories.ProductRepository;
import nicoAntonelli.managefy.repositories.SupplierRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final BusinessService businessService; // Dependency
    private final ProductRepository productRepository; // Dependency

    @Autowired
    public SupplierService(SupplierRepository supplierRepository,
                           BusinessService businessService,
                           ProductRepository ProductRepository) {
        this.supplierRepository = supplierRepository;
        this.businessService = businessService;
        this.productRepository = ProductRepository;
    }

    public List<Supplier> GetSuppliers(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return supplierRepository.findActivesByBusiness(businessID);
    }

    // Note: this method needs to be called with a supplier with products already-validated
    public Boolean ExistsSupplier(Supplier supplier, User user) {
        // Get businessID from first element (
        Long businessID = supplier.getProducts().iterator().next().getBusiness().getId();

        return ExistsSupplier(supplier.getId(), businessID, user);
    }

    public Boolean ExistsSupplier(Long supplierID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return supplierRepository.existsByIdActiveAndBusiness(supplierID, businessID);
    }

    public Supplier GetOneSupplier(Long supplierID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        Optional<Supplier> supplier = supplierRepository.findByIdActiveAndBusiness(supplierID, businessID);
        if (supplier.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneSupplier' - Supplier with ID: " + supplierID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        return supplier.get();
    }

    // On a new product context
    public Supplier CreateSupplierForNewProduct(Supplier supplier) {
        // Validate name
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSupplier' - Name field was not supplied");
        }

        // Forced initial state
        supplier.setId(null);
        supplier.setDeletionDate(null);
        supplier.setProducts(null);

        return supplierRepository.save(supplier);
    }

    public Supplier CreateSupplier(SupplierCU supplierCU, User user) {
        // Convert DTO into supplier with products
        Supplier supplier = supplierCU.getSupplier();
        supplier.loadProducts(supplierCU.getProducts(), supplierCU.getBusinessID());

        // Validate name
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSupplier' - Name field was not supplied");
        }

        // Validate associated products
        ValidateProductsForSupplier(supplier, user);

        // Forced initial state
        supplier.setId(null);
        supplier.setDeletionDate(null);

        // Don't save products as nested
        supplier.setProducts(null);

        // Save supplier first
        supplier = supplierRepository.save(supplier);

        // Update each (complete) product and save it
        Set<Product> products = new HashSet<>();
        for (Long id : supplierCU.getProducts()) {
            Product product = productRepository.findById(id).orElseThrow(
                    () -> new Exceptions.BadRequestException("Error at 'CreateSupplier' - Problem validating product with ID: " + id)
            );

            product.setSupplierByID(supplier.getId());
        }

        productRepository.saveAll(products);

        return supplier;
    }

    public Supplier UpdateSupplier(SupplierCU supplierCU, User user) {
        // Convert DTO into supplier with products
        Supplier supplier = supplierCU.getSupplier();
        supplier.loadProducts(supplierCU.getProducts(), supplierCU.getBusinessID());

        // Validate name and logic deletion
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSupplier' - Name field was not supplied");
        }
        if (supplier.getDeletionDate() != null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSupplier' - Supplier can't have a deletion date");
        }

        // Validate associated products
        ValidateProductsForSupplier(supplier, user);

        // Validate supplier existence and obtain it loaded from DB
        Supplier realSupplier = GetOneSupplier(supplier.getId(), supplierCU.getBusinessID(), user);

        // Merge DTO's supplier with the original - Don't mess up relation w/products
        realSupplier.setName(supplier.getName());
        realSupplier.setEmail(supplier.getEmail());
        realSupplier.setPhone(supplier.getPhone());
        realSupplier.setDescription(supplier.getDescription());

        // Save supplier first
        supplier = supplierRepository.save(realSupplier);

        // Update each (complete) product and save it
        Set<Product> products = new HashSet<>();
        for (Long id : supplierCU.getProducts()) {
            Product product = productRepository.findById(id).orElseThrow(
                    () -> new Exceptions.BadRequestException("Error at 'UpdateSupplier' - Problem validating product with ID: " + id)
            );

            product.setSupplierByID(supplier.getId());
        }

        productRepository.saveAll(products);

        return supplier;
    }

    // Logic deletion (field: deletion date)
    public Long DeleteSupplier(Long supplierID, Long businessID, User user) {
        // Validate admin role
        boolean exists = businessService.ExistsBusiness(businessID, user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteSupplier' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't an Admin or the Manager");
        }

        Supplier supplier = GetOneSupplier(supplierID, businessID, user);
        supplier.setDeletionDate(LocalDateTime.now());
        supplierRepository.save(supplier);

        return supplierID;
    }

    private void ValidateProductsForSupplier(Supplier supplier, User user) {
        // At least one product
        Set<Product> products = supplier.getProducts();
        if (products == null || products.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - No product was supplied");
        }

        // Products need to exist and each product associated with the same business
        Long businessID = 0L;
        for (Product product : products) {
            // First loop
            if (businessID == 0L) businessID = product.getBusiness().getId();

            if (!productRepository.existsByIdActiveAndBusiness(product.getId(), user.getId())) {
                throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - Product doesn't exist or it's not associated with the user: " + user.getId());
            }

            // Check every product's businessID
            if (!Objects.equals(businessID, product.getBusiness().getId())) {
                throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - Each product needs to be associated with the same business");
            }
        }

        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);
    }
}
