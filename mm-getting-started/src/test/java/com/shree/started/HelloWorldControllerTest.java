package com.shree.started;


import com.shree.started.hello.HelloRecord;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;


@MicronautTest
class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
            @Client("/")
    HttpClient httpClient;

    @Test
    void testAppRunning() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testTextResponse() {
        var response = httpClient.toBlocking().exchange("/hello", String.class);
        Assertions.assertEquals(200, response.getStatus().getCode());
        Assertions.assertEquals("Hello World", response.body());
    }

    @Test
    void testJSONResponse() {
        var msg = "Hello World from Dockerized Micronaut App";
        var response = httpClient.toBlocking().exchange("/hello/json", HelloRecord.class);
        Assertions.assertEquals(200, response.getStatus().getCode());
        Assertions.assertEquals(msg, response.body().msg());
    }

}
