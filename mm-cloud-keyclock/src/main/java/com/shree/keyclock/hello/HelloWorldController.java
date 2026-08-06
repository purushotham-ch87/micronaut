package com.shree.keyclock.hello;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

import java.util.Map;

@Controller("/hello")
//@Secured(SecurityRule.IS_AUTHENTICATED)
public class HelloWorldController {

    @Get(produces = MediaType.TEXT_PLAIN)
    @Secured("hello-get")
    public MutableHttpResponse<Object> index() {
        return HttpResponse.status(HttpStatus.OK).body("Hello World");
    }

    @Get(uri = "/json", produces = MediaType.APPLICATION_JSON)
    @Secured("hello-get-json")
    public HttpResponse<HelloRecord> indexJSON() {
        HelloRecord helloRecord = new HelloRecord("Hello World from Dockerized Micronaut App");
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get("/debug")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public Map<String, Object> debug(Authentication authentication) {
        return Map.of(
                "name", authentication.getName(),
                "roles", authentication.getRoles(),
                "attributes", authentication.getAttributes()
        );
        //return "authenticated";
    }
}
