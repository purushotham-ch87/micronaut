package com.shree.cloudweb.product;


import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@MicronautTest
class ProductControllerTest {

    private final Logger logger = LoggerFactory.getLogger(ProductControllerTest.class);

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/product")
    private HttpClient client;

    @Inject
    private JsonMapper jsonMapper;


    @Test
    void testProductController() throws IOException {
        // You can add tests for your ProductController here
        // For example, you can send HTTP requests to the controller and assert the responses
        JsonNode response = client.toBlocking().retrieve("/list", JsonNode.class); // Example request to the root endpoint of ProductController
        //assertions.assertTrue(application.isRunning());
        logger.info("response: {}", jsonMapper.writeValueAsString(response));
        Assertions.assertNotNull(client);
    }

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

}
