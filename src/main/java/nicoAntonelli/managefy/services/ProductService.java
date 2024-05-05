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

    @Autowired
    public ProductService(ProductRepository productRepository, BusinessService businessService) {
        this.productRepository = productRepository;
        this.businessService = businessService;
    }

    public List<Product> GetProducts() {
        return productRepository.findAll();
    }

    public Boolean ExistsProduct(Long productID) {
        return productRepository.existsById(productID);
    }

    public Product GetOneProduct(Long productID) {
        Optional<Product> product = productRepository.findById(productID);
        if (product.isEmpty()) {
            throw new IllegalStateException("Error at 'GetOneProduct' - Product with ID: " + productID + " doesn't exist");
        }

        return product.get();
    }

    public Product CreateProduct(Product product) {
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
    }

    public Product UpdateProduct(Product product) {
        boolean exists = ExistsProduct(product.getId());
        if (!exists) {
            throw new IllegalStateException("Error at 'UpdateProduct' - Product with ID: " + product.getId() + " doesn't exist");
        }

        return productRepository.save(product);
    }

    // Logic deletion (field: deletion date)
    public Product DeleteProduct(Long productID) {
        Product product = GetOneProduct(productID);
        product.setDeletionDate(new Date());
        productRepository.save(product);

        return product;
    }
}
