package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.entities.dto.Result;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SuppressWarnings("unused")
@RequestMapping(path = "api/products")
public class ProductController {
    private final ProductService productService;
    private final ErrorLogService errorLogService; // Dependency

    @Autowired
    public ProductController(ProductService productService, ErrorLogService errorLogService) {
        this.productService = productService;
        this.errorLogService = errorLogService;
    }

    @GetMapping
    public Result<List<Product>> GetProducts() {
        try {
            List<Product> products = productService.GetProducts();
            return new Result<>(products);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @GetMapping(path = "{productID}")
    public Result<Product> GetOneProduct(@PathVariable("productID") Long productID) {
        try {
            Product product = productService.GetOneProduct(productID);
            return new Result<>(product);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PostMapping
    public Result<Product> CreateProduct(@RequestBody Product product) {
        try {
            product = productService.CreateProduct(product);
            return new Result<>(product);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @PutMapping
    public Result<Product> UpdateProduct(@RequestBody Product product) {
        try {
            product = productService.UpdateProduct(product);
            return new Result<>(product);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }

    @DeleteMapping(path = "{productID}")
    public Result<Product> DeleteProduct(@PathVariable("productID") Long productID) {
        try {
            Product product = productService.DeleteProduct(productID);
            return new Result<>(product);
        } catch (IllegalStateException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400);
        } catch (SecurityException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401);
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500);
        }
    }
}
