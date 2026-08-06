package com.shree.openbao.openbao;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AppRoleLoginRequest(String role_id, String secret_id) {}