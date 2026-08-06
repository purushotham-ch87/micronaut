package com.shree.openbao.openbao;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;

@Singleton
public class OpenBaoService {

    private final Logger log = LoggerFactory.getLogger(OpenBaoService.class);
    private final OpenBaoApi api;
    private final String roleId;
    private final String secretId;

    public OpenBaoService(OpenBaoApi api, @Value("${openbao.role-id}") String roleId, @Value("${openbao.secret-id}") String secretId) {
        this.api = api;
        this.roleId = roleId;
        this.secretId = secretId;
    }

    public Mono<Map<String, String>> getDbSecret() {
        log.debug("getDbSecret");
        log.debug("roleId: " + roleId);
        log.debug("secretId: " + secretId);
        return api.login(new AppRoleLoginRequest(roleId, secretId))
                .doOnNext(resp -> log.debug("Login Response: {}", resp)) // Check login tokens
                .doOnError(err -> log.error("Login Failed: {}", err.getMessage()))
                .flatMap(resp ->
                        api.readSecret(resp.auth().client_token(), "app/db"))
                .doOnNext(resp -> log.debug("Secret Response: {}", resp)) // Check secret contents
                .doOnError(err -> log.error("Secret Read Failed: {}", err.getMessage()))
                .map(resp -> resp.data().data())
                .doOnNext(data -> log.debug("Extracted Data: {}.", data));
    }
}