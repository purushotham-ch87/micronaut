package com.shree.cloudweb.product;

public class MinPriceRule implements ProductSearchRule {
    @Override
    public boolean matches(Product product, SearchFilter criteria) {
        Double minPrice = criteria.minPrice();
        if (minPrice == null) {
            return true; // No minimum price specified, so it matches
        }
        return product.price() >= minPrice;
    }
}
