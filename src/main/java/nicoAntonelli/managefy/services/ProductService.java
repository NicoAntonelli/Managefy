package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.Supplier;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.ProductRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessService businessService; // Dependency
    private final SupplierService supplierService; // Dependency

    @Autowired
    public ProductService(ProductRepository productRepository,
                          BusinessService businessService,
                          SupplierService supplierService) {
        this.productRepository = productRepository;
        this.businessService = businessService;
        this.supplierService = supplierService;
    }

    public List<Product> GetProducts(Long businessID, User user) {
        // Validate business, user and role
        businessService.GetOneBusiness(businessID, user);

        return productRepository.findActivesByBusiness(businessID);
    }

    public Boolean ExistsProduct(Product product, User user) {
        // Product has ID
        if (product.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'ExistsProduct' - Product not supplied");
        }

        // Business not null
        Business business = product.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'ExistsProduct' - Business not supplied");
        }

        return ExistsProduct(product.getId(), business.getId(), user);
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

    public Product CreateProduct(Product product, User user) {
        // Validate required and optional simple attributes
        ValidateSimpleAttributesForProduct(product);

        // Validate associated business
        Business business = product.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(business.getId(), user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business with ID: " + business.getId() + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        // Optional associated supplier - check it or create a new one (and update product)
        Supplier supplier = product.getSupplier();
        if (supplier != null) {
            CheckOrCreateSupplierForProduct(product, user);
        }

        // Forced initial state
        product.setId(null);
        product.setDeletionDate(null);

        return productRepository.save(product);
    }

    public Product UpdateProduct(Product product, User user) {
        // Validate required and optional simple attributes
        ValidateSimpleAttributesForProduct(product);

        // Validate associated business
        Business business = product.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(business.getId(), user, "collaborator")) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Business with ID: " + business.getId() + " doesn't exist or it's not associated with the user: " + user.getId());
        }

        // Validate product existence
        boolean exists = ExistsProduct(product.getId(), business.getId(), user);
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Product with ID: " + product.getId() + " doesn't exist or it's not associated with the business: " + business.getId());
        }

        // Optional associated supplier - check it or create a new one (and update product)
        Supplier supplier = product.getSupplier();
        if (supplier != null) {
            CheckOrCreateSupplierForProduct(product, user);
        }

        return productRepository.save(product);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteProduct(Long productID, Long businessID, User user) {
        Product product = GetOneProduct(productID, businessID, user);
        product.setDeletionDate(LocalDateTime.now());
        productRepository.save(product);

        return productID;
    }

    private void ValidateSimpleAttributesForProduct(Product product) {
        if (product.getDeletionDate() != null) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Product can't have a deletion date");
        }

        if (product.getUnitCost() == null || product.getUnitPrice() == null || product.getStock() == null) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - One or more of the required fields were not supplied");
        }

        if (product.getUnitCost() < 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Unit cost can't be negative");
        }

        if (product.getUnitPrice() < 0 || product.getUnitPrice() < product.getUnitCost()) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Unit price can't be negative or lesser than unit cost");
        }

        if (product.getStock() < 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Stock can't be negative");
        }

        if (product.getStockMin() != null && product.getStockMin() <= 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Optional stock min can't be negative or zero");
        }

        if (product.getSaleMinAmount() != null && product.getSaleMinAmount() <= 0) {
            throw new Exceptions.BadRequestException("Error at 'ValidateSimpleAttributesForProduct' - Optional sale's minimum amount can't be negative or zero");
        }
    }

    private void CheckOrCreateSupplierForProduct(Product product, User user) {
        Supplier supplier = product.getSupplier();

        // Supplier not supplied is OK
        if (supplier == null) return;

        // With ID: validate it
        if (supplier.getId() != null) {
            Business business = product.getBusiness();
            boolean exists = supplierService.ExistsSupplier(supplier.getId(), business.getId(), user);
            if (!exists) {
                throw new Exceptions.BadRequestException("Error at 'CheckOrCreateSupplierForProduct' - Optional supplier: " + supplier.getId() + " supplied it's not valid");
            }

            return;
        }

        // Without ID: create it, then set it updated in product
        supplier = supplierService.CreateSupplier(supplier, user);
        product.setSupplier(supplier);
    }
}
