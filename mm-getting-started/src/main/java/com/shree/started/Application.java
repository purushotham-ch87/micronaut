package com.shree.started;

import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);

        // Log a message indicating that the application has started
        logger.info("Application has started successfully.");

        logger.info("Context can be accessed using: ApplicationContext applicationContext = Micronaut.build().start();");

        //logger.info("Micronaut.run() method is used to start the application and initialize the application context and returns ApplicationContext as value which is implemented as child class of Interface ApplicationContext.");
    }
}