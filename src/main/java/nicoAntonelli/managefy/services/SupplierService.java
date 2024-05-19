package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.NotificationC;
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
    private final NotificationService notificationService; // Dependency
    private final ProductRepository productRepository; // Dependency

    @Autowired
    public SupplierService(SupplierRepository supplierRepository,
                           BusinessService businessService,
                           NotificationService notificationService,
                           ProductRepository ProductRepository) {
        this.supplierRepository = supplierRepository;
        this.businessService = businessService;
        this.notificationService = notificationService;
        this.productRepository = ProductRepository;
    }

    public List<Supplier> GetSuppliers(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return supplierRepository.findActivesByBusiness(businessID);
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
    public Supplier CreateSupplierForNewProduct(SupplierCU supplierCU, User user) {
        // Validate name
        if (supplierCU.getName() == null || supplierCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSupplierForNewProduct' - Name field was not supplied");
        }

        // New supplier object with DTO info
        Supplier supplier = new Supplier(supplierCU.getName(), supplierCU.getDescription(),
                                         supplierCU.getEmail(), supplierCU.getPhone());

        supplier = supplierRepository.save(supplier);

        // Notification for new supplier
        NotificationC notification = new NotificationC("Your new supplier '" + supplierCU.getName() + "' was created successfully", "low");
        notificationService.CreateNotification(notification, user);

        return supplier;
    }

    public Supplier CreateSupplier(SupplierCU supplierCU, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(supplierCU.getBusinessID(), user);

        // Validate name
        if (supplierCU.getName() == null || supplierCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'CreateSupplier' - Name field was not supplied");
        }

        // Validate associated products' IDs and load each in a collection
        Set<Product> products = ValidateProductsForSupplier(supplierCU, user);

        // New supplier object with DTO info
        Supplier supplier = new Supplier(supplierCU.getName(), supplierCU.getDescription(),
                                         supplierCU.getEmail(), supplierCU.getPhone());

        // Save supplier first (without products as nested)
        supplier = supplierRepository.save(supplier);

        // Set new supplier ID for every product
        for (Product product : products) {
            product.setSupplierByID(supplier.getId());
        }

        // Save products with supplier set
        productRepository.saveAll(products);

        // Notification for new supplier
        NotificationC notification = new NotificationC("Your new supplier '" + supplierCU.getName() + "' was created successfully", "low");
        notificationService.CreateNotification(notification, user);

        return supplier;
    }

    public Supplier UpdateSupplier(SupplierCU supplierCU, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(supplierCU.getBusinessID(), user);

        // Validate name
        if (supplierCU.getName() == null || supplierCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'UpdateSupplier' - Name field was not supplied");
        }

        // Validate associated products' IDs and load each in a collection
        Set<Product> products = ValidateProductsForSupplier(supplierCU, user);

        // Validate supplier existence and obtain it loaded from DB
        Supplier supplier = GetOneSupplier(supplierCU.getId(), supplierCU.getBusinessID(), user);

        // Merge DTO's supplier with the original - Don't mess up relation w/products
        supplier.setName(supplierCU.getName());
        supplier.setEmail(supplierCU.getEmail());
        supplier.setPhone(supplierCU.getPhone());
        supplier.setDescription(supplierCU.getDescription());

        // Save supplier first (without products as nested)
        supplier = supplierRepository.save(supplier);

        // Set existent supplier ID for every product
        for (Product product : products) {
            product.setSupplierByID(supplier.getId());
        }

        // Save products with supplier set
        productRepository.saveAll(products);

        // Notification for update supplier
        NotificationC notification = new NotificationC("Your supplier '" + supplierCU.getName() + "' was updated successfully", "low");
        notificationService.CreateNotification(notification, user);

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

        // Erase supplier for associated products too
        List<Product> products = productRepository.findActivesByBusinessAndSupplier(businessID, supplierID);
        for (Product product : products) {
            product.setSupplier(null);
        }
        productRepository.saveAll(products);

        // Notification for delete supplier
        NotificationC notification = new NotificationC("Your supplier was deleted successfully, and erased from associated products", "low");
        notificationService.CreateNotification(notification, user);

        return supplierID;
    }

    // On a delete product context
    public void DeleteSupplierAfterDeleteProduct(Long supplierID, User user) {
        supplierRepository.deleteById(supplierID);

        // Notification for delete supplier
        NotificationC notification = new NotificationC("After your product elimination, the supplier without products was also deleted successfully", "low");
        notificationService.CreateNotification(notification, user);
    }

    private Set<Product> ValidateProductsForSupplier(SupplierCU supplierCU, User user) {
        // At least one product
        Set<Long> productsIDs = supplierCU.getProductsIDs();
        if (productsIDs == null || productsIDs.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - No product was supplied");
        }

        // Validate and retrieve the products fully-loaded from DB
        Set<Product> products = new HashSet<>();
        for (Long id : productsIDs) {
            // Check product existence & business
            Product product = productRepository.findByIdActiveAndBusiness(id, supplierCU.getBusinessID()).orElseThrow(
                    () -> new Exceptions.BadRequestException("Error at 'ValidateProductsForSupplier' - Product doesn't exist or it's not associated with the user: " + user.getId())
            );

            products.add(product);
        }

        return products;
    }
}
