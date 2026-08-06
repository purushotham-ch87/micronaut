package com.shree.cloudnative.hello;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/hello")
public class HelloWorldController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public MutableHttpResponse<Object> index() {
        return HttpResponse.status(HttpStatus.OK).body("Hello World");
    }

    @Get(uri = "/json", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSON() {
        HelloRecord helloRecord = new HelloRecord("Hello World from Dockerized Micronaut App");
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }
}
