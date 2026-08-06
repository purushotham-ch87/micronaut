package com.shree.openbao.openbao;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller("/config-nonblocking")
public class ConfigControllerNonBlocking {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenBaoService openBaoService;

    public ConfigControllerNonBlocking(OpenBaoService openBaoService) {
        this.openBaoService = openBaoService;
    }

    @Get("/db")
    public Mono<Map<String, String>> db() {
        log.debug("db");
        return openBaoService.getDbSecret();
    }
}