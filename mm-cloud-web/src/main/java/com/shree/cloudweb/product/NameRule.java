package com.shree.cloudweb.product;

public class NameRule implements ProductSearchRule {
    @Override
    public boolean matches(Product product, SearchFilter criteria) {
        String name = criteria.name();
        if (name == null || name.isEmpty()) {
            return true; // No name specified, so it matches
        }
        return product.name().toLowerCase().contains(name.toLowerCase());
    }
}
