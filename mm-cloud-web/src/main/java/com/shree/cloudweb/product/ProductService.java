package com.shree.cloudweb.product;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final InMemoryProductRepository productRepository;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "InMemoryProductRepository is injected and managed by Micronaut as a singleton bean."
    )
    public ProductService(InMemoryProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductStatus> addProduct(List<Product> productList) {
        LOG.debug("Adding product {}", productList);
        return productList.stream()
                .map(product -> productRepository.addProduct(product))
                .toList();
    }

    public Product getProductById(long id) {
        // Logic to retrieve the product by ID from the database or in-memory storage
        LOG.debug("Getting product by id {}.", id);
        return productRepository.getProductStore().values().stream().filter(product -> product.id() == id).findFirst().orElse(null); // Replace with actual retrieval logic
    }

    public List<Product> getProductsByName(String name) {
        // Logic to retrieve products by name from the database or in-memory storage
        LOG.debug("Getting products by name {}.", name);
        return productRepository.getProductStore().values().stream().filter(product -> product.name().equalsIgnoreCase(name)).collect(Collectors.toList()); // Replace with actual retrieval logic; // Replace with actual retrieval logic
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        // Logic to retrieve products within the specified price range from the database or in-memory storage
        LOG.debug("Getting products by price range {} & {}.", minPrice, maxPrice);
        return productRepository.getProductStore().values().stream().filter(product -> product.price() >= minPrice && product.price() <= maxPrice).collect(Collectors.toList()); // Replace with actual retrieval logic
    }

    public List<Product> searchProducts(String name, double minPrice, double maxPrice) {
        // Logic to retrieve products within the specified price range from the database or in-memory storage
        LOG.debug("Searching products by name {} & {}.", name, minPrice, maxPrice);
        return productRepository.getProductStore().values().stream().filter(product -> (minPrice == 0 || product.price() >= minPrice) && (maxPrice==0 || product.price() <= maxPrice) && name.equalsIgnoreCase(product.name())).collect(Collectors.toList()); // Replace with actual retrieval logic
    }

    public List<Product> getAllProducts() {
        // Logic to retrieve all products from the database or in-memory storage
        LOG.debug("Getting all products.");
        return productRepository.getProductStore().values().stream().toList(); // Example retrieval logic
         // Replace with actual retrieval logic
    }

    public ProductStatus deleteProductById(long id) {
        // Logic to delete the product by ID from the database or in-memory storage
        LOG.debug("Deleting product by id {}.", id);
        return productRepository.deleteProduct(id); // Replace with actual deletion logic
    }

    public List<ProductStatus> deleteProductByList(List<Long> ids) {
        // Logic to delete products by a list of IDs from the database or in-memory storage
        LOG.debug("Deleting products by ids {}.", ids);
        List<ProductStatus> productStatusList =  ids.stream().map(this::deleteProductById).toList(); // Delete each product by ID
        return productStatusList; // Replace with actual deletion logic
    }

    public List<ProductStatus> updateProduct(List<Product> updatedProductList) {
        // Logic to update the product in the database or in-memory storage
        LOG.debug("Updating product {}.", updatedProductList);
        return updatedProductList.stream()
                .map(product -> productRepository.updateProduct(product.id(), product))
                .toList();

    }

}
