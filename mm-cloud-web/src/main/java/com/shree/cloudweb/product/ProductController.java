package com.shree.cloudweb.product;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller("/product")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "ProductService is injected and managed by Micronaut as a singleton bean."
    )
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> productById(long id) {
        log.debug("Getting product by id {}.", id);
        Product products = productService.getProductById(id);
        if (products == null) {
            log.error("Product with id {} not found.", id);
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.NOT_FOUND.getCode(), "Product not found", null, null);
            return HttpResponse.status(HttpStatus.NOT_FOUND).body(productResponse);
        } else {
            log.debug("Product with id {} found.", id);
            ProductResponse<Product> productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Product found", products, null);
            return HttpResponse.status(HttpStatus.OK).body(productResponse);
        }
    }

    @Get(uri = "/list", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> listProducts() {
        log.debug("Getting product list.");
        List<Product> products = productService.getAllProducts();
        if(products.isEmpty()) {
            log.error("Product list is empty.");
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.NOT_FOUND.getCode(), "Products data store is empty", null, null);
            return HttpResponse.status(HttpStatus.NOT_FOUND).body(productResponse);
        } else {
            log.debug("Product list found.");
            ProductResponse<List<Product>> productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products list found", products, null);
            return HttpResponse.status(HttpStatus.OK).body(productResponse);
        }
    }

    @Post(uri = "/add", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> createProducts(@Body List<Product> productList) {
        log.info("Creating product {}.", productList);
        if(productList == null || productList.isEmpty()) {
            log.error("Product JSON in request is empty.");
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.BAD_REQUEST.getCode(), "Invalid Data: No product details in JSON in request.", null, null);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(productResponse);
        } else {
            log.debug("Product {} created.", productList);
            List<ProductStatus> productCreationStatusList = productService.addProduct(productList);
            ProductResponse<List<ProductStatus>> productResponse = new ProductResponse<>(HttpStatus.CREATED.getCode(), "Product List added to the data store. For final status refer specific records", productCreationStatusList, null);
            return HttpResponse.status(HttpStatus.CREATED).body(productResponse);
        }

    }

    @Put(uri = "/update", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> updateProducts(@Body List<Product> product) {
        log.debug("Updating product {}", product);

        if(product == null || product.isEmpty()) {
            log.error("Product JSON in request is empty.");
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.BAD_REQUEST.getCode(), "Invalid Data: No product details in JSON in request.", null, null);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(productResponse);
        } else {
            log.debug("Product {} list updated.", product);
            List<ProductStatus> productUpdationStatus = productService.updateProduct(product);
            ProductResponse<List<ProductStatus>> productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Update ID List executed successfully, refer data object for final status.", productUpdationStatus, null);
            return HttpResponse.status(HttpStatus.OK).body(productResponse);
        }
    }

    @Delete(uri = "/delete/{id}", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> deleteProductById(long id) {
        log.debug("Deleting product with id {}.", id);
        if(id <= 0) {
            log.error("Product ID is invalid for deletion.");
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.BAD_REQUEST.getCode(), "Invalid Data: Product ID is required for deletion.", null, null);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(productResponse);
        } else {
            ProductStatus productDeletionStatus = productService.deleteProductById(id);
            log.debug("Product with id {} deleted.", id);
            ProductResponse<ProductStatus> productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), productDeletionStatus.msg(), productDeletionStatus, null);
            return HttpResponse.status(HttpStatus.OK).body(productResponse);
        }
    }

    @Delete(uri = "/delete", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> deleteProductById(@Body  List<Long> idList) {
        log.info("Deleting products with id {}.", idList);
        if(idList.isEmpty()) {
            log.error("Product ID list is empty.");
            ProductResponse<?> productResponse = new ProductResponse<>(HttpStatus.BAD_REQUEST.getCode(), "Invalid Data: Product ID is required for deletion.", null, null);
            return HttpResponse.status(HttpStatus.BAD_REQUEST).body(productResponse);
        } else {
            log.debug("Product ID list is {}.", idList);
            List<ProductStatus> productDeletionStatusList = productService.deleteProductByList(idList);
            ProductResponse<List<ProductStatus>> productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Deletion ID List executed successfully, refer data object for final status.", productDeletionStatusList, null);
            return HttpResponse.status(HttpStatus.OK).body(productResponse);
        }
    }

    //Query Parameters: /search?name=ProductName or /search?minPrice=10&maxPrice=100
    @Get(uri = "/search{?name,minPrice,maxPrice}", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> searchProducts(@QueryValue @Nullable String name, @QueryValue @Nullable Double minPrice, @QueryValue @Nullable Double maxPrice) {
        log.debug("Searching products with name: {}, minPrice: {}, maxPrice: {}", name, minPrice, maxPrice);
        List<Product> products;
        ProductResponse<List<Product>> productResponse;
        if(name != null && !name.isEmpty() && minPrice != null && maxPrice != null) {
            products = productService.searchProducts(name, minPrice, maxPrice);
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given name and price range.", products, null);
        } else if (name != null && !name.isEmpty() && minPrice == null && maxPrice == null) {
            products = productService.getProductsByName(name);
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given name.", products, null);
        } else if (name == null && minPrice != null && maxPrice != null) {
            products = productService.getProductsByPriceRange(minPrice, maxPrice);
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given price range.", products, null);
        } else if (name != null && !name.isEmpty() && minPrice != null && maxPrice == null) {
            products = productService.searchProducts(name, minPrice, 0);
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given name and minimum price.", products, null);
        } else if (name != null && !name.isEmpty() && minPrice == null && maxPrice != null) {
            products = productService.searchProducts(name, 0, maxPrice);
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given name and maximum price.", products, null);
        } else {
            log.info("No search parameters provided. Returning all products.");
            products = productService.getAllProducts();
            productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "All products returned.", products, null);
        }
        return HttpResponse.status(HttpStatus.OK).body(productResponse);
    }

    //Query Parameters: /search?name=ProductName or /search?minPrice=10&maxPrice=100 using Switch case for better readability
    @Get(uri = "/search-v2{?name,minPrice,maxPrice}", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> searchProductsV2(@QueryValue @Nullable  String name, @QueryValue @Nullable Double minPrice, @QueryValue @Nullable Double maxPrice) {
        log.debug("Searching products with name: {}, minPrice: {}, maxPrice: {} using switch case for better readability", name, minPrice, maxPrice);
        SearchFilter searchFilter = new SearchFilter(name, minPrice, maxPrice);
        List<Product> products = switch (searchFilter) {
            case SearchFilter sf when sf.name() != null && !sf.name().isEmpty() && sf.minPrice() != null && sf.maxPrice() != null ->
                    productService.searchProducts(name, minPrice, maxPrice);
            case SearchFilter sf when sf.name() != null && !sf.name().isEmpty() && sf.minPrice() == null && sf.maxPrice() == null ->
                    productService.getProductsByName(name);
            case SearchFilter sf when sf.name() == null && sf.minPrice() != null && sf.maxPrice() != null ->
                    productService.getProductsByPriceRange(minPrice, maxPrice);
            case SearchFilter sf when sf.name() != null && !sf.name().isEmpty() && sf.minPrice() != null && sf.maxPrice() == null ->
                    productService.searchProducts(name, minPrice, 0);
            case SearchFilter sf when sf.name() != null && !sf.name().isEmpty() && sf.minPrice() == null && sf.maxPrice() != null ->
                    productService.searchProducts(name, 0, maxPrice);
            default -> {
                log.info("No search parameters provided. Returning all products in search v2.");
                products = productService.getAllProducts();
                yield products;
            }
        };
        ProductResponse<List<Product>>  productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given search parameters.", products, null);
        return HttpResponse.status(HttpStatus.OK).body(productResponse);
    }


    //Query Parameters: /search?name=ProductName or /search?minPrice=10&maxPrice=100 using Rule Engine for better readability and maintainability
    @Get(uri = "/search-v3{?name,minPrice,maxPrice}", produces = MediaType.APPLICATION_JSON)
    public MutableHttpResponse<ProductResponse> searchProductsV3(@QueryValue @Nullable String name, @QueryValue @Nullable Double minPrice, @QueryValue @Nullable Double maxPrice) {
        log.debug("Searching products with name: {}, minPrice: {}, maxPrice: {} using rule engine for better readability and maintainability", name, minPrice, maxPrice);

        List<ProductSearchRule> rules = List.of(new MinPriceRule(), new MaxPriceRule(), new NameRule());

        SearchFilter searchFilter = new SearchFilter(name, minPrice, maxPrice);
        List<Product> products = productService.getAllProducts().stream()
                .filter(product -> rules.stream().allMatch(rule -> rule.matches(product, searchFilter)))
                .toList();

        ProductResponse<List<Product>>  productResponse = new ProductResponse<>(HttpStatus.OK.getCode(), "Products found for the given search parameters.", products, null);
        return HttpResponse.status(HttpStatus.OK).body(productResponse);
    }


}
