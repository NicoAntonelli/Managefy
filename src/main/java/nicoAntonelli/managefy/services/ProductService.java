package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.entities.dto.NotificationC;
import nicoAntonelli.managefy.entities.dto.ProductCU;
import nicoAntonelli.managefy.entities.dto.SupplierCU;
import nicoAntonelli.managefy.repositories.ProductRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessService businessService; // Dependency
    private final NotificationService notificationService; // Dependency
    private final SupplierService supplierService; // Dependency

    @Autowired
    public ProductService(ProductRepository productRepository,
                          BusinessService businessService,
                          NotificationService notificationService,
                          SupplierService supplierService) {
        this.productRepository = productRepository;
        this.businessService = businessService;
        this.notificationService = notificationService;
        this.supplierService = supplierService;
    }

    public List<Product> GetProducts(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return productRepository.findActivesByBusiness(businessID);
    }

    public List<Product> GetProductsBySupplier(Long businessID, Long supplierID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return productRepository.findActivesByBusinessAndSupplier(businessID, supplierID);
    }

    public Boolean ExistsProduct(Long productID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return productRepository.existsByIdActiveAndBusiness(productID, businessID);
    }

    public Product GetOneProduct(Long productID, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        Optional<Product> product = productRepository.findByIdActiveAndBusiness(productID, businessID);
        if (product.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneProduct' - Product with ID: " + productID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        return product.get();
    }

    public Product CreateProduct(ProductCU productCU, User user) {
        // Validate required and optional simple attributes
        ValidateSimpleAttributesForProduct(productCU);

        // Validate associated business
        Long businessID = productCU.getBusinessID();
        if (businessID == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(businessID, user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business with ID: " + businessID + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        // Optional associated supplier - check it or create a new one
        CheckOrCreateSupplierForProduct(productCU, user);

        // New product object with DTO info
        Product product = new Product(productCU.getCode(), productCU.getName(), productCU.getDescription(),
                productCU.getUnitCost(), productCU.getUnitPrice(), productCU.getStock(),
                productCU.getStockMin(), productCU.getSaleMinAmount());

        // Set business
        product.setBusinessByID(businessID);

        // Update product with supplier
        if (productCU.getSupplier() != null) {
            product.setSupplierByID(productCU.getSupplier().getId());
        }

        product = productRepository.save(product);

        // Notification for new product
        NotificationC notification = new NotificationC("Your new product '" + productCU.getCode() + " - " + productCU.getName() + "' was created successfully", "low");
        notificationService.CreateNotification(notification, user);

        return product;
    }

    public Product UpdateProduct(ProductCU productCU, User user) {
        // Validate required and optional simple attributes
        ValidateSimpleAttributesForProduct(productCU);

        // Validate associated business
        Long businessID = productCU.getBusinessID();
        if (businessID == null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(businessID, user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Business with ID: " + businessID + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        // Validate product existence and obtain it loaded from DB
        Product product = GetOneProduct(productCU.getId(), productCU.getBusinessID(), user);

        // Optional associated supplier - check it or create a new one
        CheckOrCreateSupplierForProduct(productCU, user);

        // Merge DTO's product with the original - Don't mess up relations w/other entities
        product.setCode(productCU.getCode());
        product.setName(productCU.getName());
        product.setDescription(productCU.getDescription());
        product.setUnitCost(productCU.getUnitCost());
        product.setUnitPrice(productCU.getUnitPrice());
        product.setStock(productCU.getStock());
        product.setStockMin(productCU.getStockMin());
        product.setSaleMinAmount(productCU.getSaleMinAmount());

        // Update product with supplier
        if (productCU.getSupplier() != null) {
            product.setSupplierByID(productCU.getSupplier().getId());
        }

        product = productRepository.save(product);

        // Notification for update product
        NotificationC notification = new NotificationC("Your product '" + productCU.getCode() + " - " + productCU.getName() + "' was updated successfully", "low");
        notificationService.CreateNotification(notification, user);

        return product;
    }

    public Product UpdateProductStock(Long productID, Long businessID, Integer stock, User user) {
        Product product = GetOneProduct(productID, businessID, user);

        if (stock <= 0) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProductStock' - Stock can't be negative");
        }

        product.setStock(stock);
        product = productRepository.save(product);

        // Notification for update product stock
        NotificationC notification = new NotificationC("The stock field for your product was updated to '" + stock + "' successfully", "low");
        notificationService.CreateNotification(notification, user);

        // Min stock threshold
        if (product.getStockMin() != null && product.getStockMin() <= product.getStock()) {
            // Notification for product min stock threshold reached
            notification = new NotificationC("Your product '" + product.getCode() + " - " + product.getName() + "' reached the minimum stock threshold of " + product.getStockMin() + ". Make sure to update its stock!", "normal");
            notificationService.CreateNotification(notification, user);
        }

        return product;
    }

    public Product UpdateOrAddSupplierForProduct(Long productID, Long businessID, Long supplierID, User user) {
        if (!supplierService.ExistsSupplier(supplierID, businessID, user)) {
            throw new Exceptions.BadRequestException("Error at 'UpdateOrAddSupplierForProduct' - Supplier with ID: " + supplierID + " doesn't exist or it's not associated with the business: " + businessID);
        }

        Product product = GetOneProduct(productID, businessID, user);
        product.setSupplierByID(supplierID);

        product = productRepository.save(product);

        // Notification for set supplier for product
        NotificationC notification = new NotificationC("Your product was updated with a supplier successfully", "low");
        notificationService.CreateNotification(notification, user);

        return product;
    }

    public Product EraseSupplierForProduct(Long productID, Long businessID, User user) {
        Product product = GetOneProduct(productID, businessID, user);
        product.setSupplier(null);

        product = productRepository.save(product);

        // Notification for erase supplier for product
        NotificationC notification = new NotificationC("Your product was updated with no supplier field successfully", "low");
        notificationService.CreateNotification(notification, user);

        return product;
    }

    // For sales
    public void UpdateProductStockByMany(Map<Long, Integer> products, Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        List<Product> productsBatch = new ArrayList<>();

        // Control stock for every product
        products.forEach((id, amountNeeded) -> {
            Product product = productRepository.findByIdActiveAndBusiness(id, businessID).orElseThrow(
                    () -> new Exceptions.BadRequestException("Error at 'UpdateProductStockByMany' - Problem validating product with ID: " + id)
            );

            // Optional - Minimum sale amount validation
            if (product.getSaleMinAmount() != null && amountNeeded < product.getSaleMinAmount()) {
                throw new Exceptions.BadRequestException("Error at 'UpdateProductStockByMany' - Product with ID: " + id + " have a minimum sale amount of " + product.getSaleMinAmount() + " units unaccomplished");
            }

            // Stock validation
            Integer currentStock = product.getStock();
            if (currentStock < amountNeeded) {
                throw new Exceptions.BadRequestException("Error at 'UpdateProductStockByMany' - Product with ID: " + id + " don't have enough stock!");
            }

            product.setStock(currentStock - amountNeeded);

            // Min stock threshold
            if (product.getStockMin() != null && product.getStockMin() <= product.getStock()) {
                // Notification for product min stock threshold reached
                NotificationC notification = new NotificationC("Your product '" + product.getCode() + " - " + product.getName() + "' reached the minimum stock threshold of " + product.getStockMin() + ". Make sure to update its stock!", "normal");
                notificationService.CreateNotification(notification, user);
            }

            productsBatch.add(product);
        });

        productRepository.saveAll(productsBatch);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteProduct(Long productID, Long businessID, User user) {
        // Validate admin role
        boolean exists = businessService.ExistsBusiness(businessID, user, "admin");
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'DeleteProduct' - Business with ID: " + businessID + " doesn't exist or the user: " + user.getId() + " isn't an Admin or the Manager");
        }

        Product product = GetOneProduct(productID, businessID, user);

        // Product's supplier (optional)
        Supplier supplier = product.getSupplier();

        // Product deletion
        product.setDeletionDate(LocalDateTime.now());
        product.setSupplier(null);
        productRepository.save(product);

        // Delete supplier if it doesn't have more associated products
        if (supplier != null) {
            List<Product> productsBySupplier = productRepository.findActivesByBusinessAndSupplier(businessID, supplier.getId());
            if (productsBySupplier.size() == 1) {
                supplierService.DeleteSupplierAfterDeleteProduct(supplier.getId(), user);
            }
        }

        // Notification for delete product
        NotificationC notification = new NotificationC("Your product " + product.getCode() + " - " + product.getName() + " was deleted successfully", "low");
        notificationService.CreateNotification(notification, user);

        return productID;
    }

    private void ValidateSimpleAttributesForProduct(ProductCU productCU) {
        if (productCU.getCode() == null || productCU.getName() == null || productCU.getDescription() == null ||
            productCU.getUnitCost() == null || productCU.getUnitPrice() == null || productCU.getStock() == null) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - One or more of the required fields were not supplied");
        }

        if (productCU.getCode().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Code can't be blank");
        }

        if (productCU.getName().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Name can't be blank");
        }

        if (productCU.getDescription().isBlank()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Description can't be blank");
        }

        if (productCU.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Unit cost can't be negative");
        }

        if (productCU.getUnitPrice().compareTo(BigDecimal.ZERO) < 0 || productCU.getUnitPrice().compareTo(productCU.getUnitCost()) < 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Unit price can't be negative or lesser than unit cost");
        }

        if (productCU.getStock() < 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Stock can't be negative");
        }

        if (productCU.getStockMin() != null && productCU.getStockMin() <= 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Optional stock min can't be negative or zero");
        }

        if (productCU.getSaleMinAmount() != null && productCU.getSaleMinAmount() <= 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Optional sale's minimum amount can't be negative or zero");
        }
    }

    private void CheckOrCreateSupplierForProduct(ProductCU productCU, User user) {
        SupplierCU supplierCU = productCU.getSupplier();

        // Supplier not supplied is OK
        if (supplierCU == null) return;

        // With ID: validate it
        if (supplierCU.getId() != null) {
            Long businessID = productCU.getBusinessID();
            boolean exists = supplierService.ExistsSupplier(supplierCU.getId(), businessID, user);
            if (!exists) {
                throw new Exceptions.BadRequestException("Error at 'CheckOrCreateSupplierForProduct' - Optional supplier: " + supplierCU.getId() + " supplied it's not valid");
            }

            return;
        }

        // Without ID: create it, then set it updated in product
        Supplier supplier = supplierService.CreateSupplierForNewProduct(supplierCU, user);
        productCU.getSupplier().setId(supplier.getId());
    }
}
