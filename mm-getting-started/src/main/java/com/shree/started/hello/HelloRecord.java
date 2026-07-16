package com.shree.started.hello;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record HelloRecord(
           String msg
) {}
