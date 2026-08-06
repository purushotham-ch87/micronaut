package com.shree.openbao.openbao;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${openbao.addr}")
public interface OpenBaoApi {

    @Post("/v1/auth/approle/login")
    Mono<AppRoleLoginResponse> login(@Body AppRoleLoginRequest request);

    @Get("/v1/secret/data/{path}")
    Mono<KvResponse> readSecret(
            @Header("X-Vault-Token") String token,
            @PathVariable String path);
}