package com.shree.cloudweb.product;

import io.micronaut.serde.annotation.Serdeable;

import java.io.Serializable;

@Serdeable
public record Product(long id, String name, String description, String url, double price) implements Serializable {
}
