package nicoAntonelli.managefy.api;

import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> GetProducts() {
        return productService.GetProducts();
    }

    @GetMapping(path = "{productID}")
    public Product GetOneProduct(@PathVariable("productID") Long productID) {
        return productService.GetOneProduct(productID);
    }

    @PostMapping
    public Product CreateProduct(@RequestBody Product product) {
        return productService.CreateProduct(product);
    }

    @PutMapping
    public Product UpdateProduct(@RequestBody Product product) {
        return productService.UpdateProduct(product);
    }

    @DeleteMapping(path = "{productID}")
    public Product DeleteProduct(@PathVariable("productID") Long productID) {
        return productService.DeleteProduct(productID);
    }
}
