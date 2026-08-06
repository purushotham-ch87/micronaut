package com.shree.cloudweb.product;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record ProductStatus(String action, String msg, Product newProduct, Product oldProduct) {
}
