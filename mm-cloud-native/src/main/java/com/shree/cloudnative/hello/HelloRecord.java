package com.shree.cloudnative.hello;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record HelloRecord(
           String msg
) {}
