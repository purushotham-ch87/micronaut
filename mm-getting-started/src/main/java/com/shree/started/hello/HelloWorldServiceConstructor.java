package com.shree.started.hello;

import jakarta.inject.Singleton;

@Singleton
public class HelloWorldServiceConstructor {

    public String helloFromService() {
        return "Hello World from service Constructor Injection";
    }

}
