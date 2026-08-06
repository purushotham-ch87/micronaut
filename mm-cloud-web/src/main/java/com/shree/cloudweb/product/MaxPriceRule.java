package com.shree.cloudweb.product;

public class MaxPriceRule implements ProductSearchRule {
    @Override
    public boolean matches(Product product, SearchFilter criteria) {
        Double maxPrice = criteria.maxPrice();
        if (maxPrice == null) {
            return true; // No maximum price specified, so it matches
        }
        return product.price() <= maxPrice;
    }
}
