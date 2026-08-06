package com.shree.openbao.openbao;


import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record AppRoleLoginResponse(Auth auth) {
    @Serdeable
    public record Auth(String client_token, int lease_duration, boolean renewable) {}
}