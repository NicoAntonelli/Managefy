package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.services.ErrorLogService;
import nicoAntonelli.managefy.services.ProductService;
import nicoAntonelli.managefy.utils.Exceptions;
import nicoAntonelli.managefy.utils.Result;
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
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @GetMapping(path = "{productID}")
    public Result<Product> GetOneProduct(@PathVariable("productID") Long productID) {
        try {
            Product product = productService.GetOneProduct(productID);
            return new Result<>(product);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PostMapping
    public Result<Product> CreateProduct(@RequestBody Product product) {
        try {
            product = productService.CreateProduct(product);
            return new Result<>(product);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @PutMapping
    public Result<Product> UpdateProduct(@RequestBody Product product) {
        try {
            product = productService.UpdateProduct(product);
            return new Result<>(product);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }

    @DeleteMapping(path = "{productID}")
    public Result<Product> DeleteProduct(@PathVariable("productID") Long productID) {
        try {
            Product product = productService.DeleteProduct(productID);
            return new Result<>(product);
        } catch (Exceptions.BadRequestException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 400, ex.getMessage());
        } catch (Exceptions.UnauthorizedException ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 401, ex.getMessage());
        } catch (Exception ex) {
            errorLogService.SetBackendError(ex.getMessage());
            return new Result<>(null, 500, ex.getMessage());
        }
    }
}
