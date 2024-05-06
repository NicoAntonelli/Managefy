package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.User;
import nicoAntonelli.managefy.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessService businessService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public ProductService(ProductRepository productRepository,
                          BusinessService businessService,
                          ErrorLogService errorLogService) {
        this.productRepository = productRepository;
        this.businessService = businessService;
        this.errorLogService = errorLogService;
    }

    public List<Product> GetProducts() {
        return productRepository.findAll();
    }

    public Boolean ExistsProduct(Long productID) {
        return productRepository.existsById(productID);
    }

    public Product GetOneProduct(Long productID) {
        try {
            Optional<Product> product = productRepository.findById(productID);
            if (product.isEmpty()) {
                throw new IllegalStateException("Error at 'GetOneProduct' - Product with ID: " + productID + " doesn't exist");
            }

            return product.get();
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Product CreateProduct(Product product) {
        try {
            // Validate associated business
            Business business = product.getBusiness();
            if (business == null || business.getId() == null) {
                throw new IllegalStateException("Error at 'CreateProduct' - Business not supplied");
            }
            if (!businessService.ExistsBusiness(business.getId())) {
                throw new IllegalStateException("Error at 'CreateProduct' - Business with ID: " + business.getId() + " doesn't exist");
            }

            product.setId(null);
            product.setDeletionDate(null);

            return productRepository.save(product);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    public Product UpdateProduct(Product product) {
        try {
            boolean exists = ExistsProduct(product.getId());
            if (!exists) {
                throw new IllegalStateException("Error at 'UpdateProduct' - Product with ID: " + product.getId() + " doesn't exist");
            }

            return productRepository.save(product);
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }

    // Logic deletion (field: deletion date)
    public Product DeleteProduct(Long productID) {
        try {
            Product product = GetOneProduct(productID);
            product.setDeletionDate(new Date());
            productRepository.save(product);

            return product;
        } catch(Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return null;
        }
    }
}
