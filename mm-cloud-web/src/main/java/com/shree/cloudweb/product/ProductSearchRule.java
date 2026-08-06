package com.shree.cloudweb.product;

public interface ProductSearchRule {
    boolean matches(Product product, SearchFilter criteria);
}
