package com.shree.started.hello;

import jakarta.inject.Singleton;

@Singleton
public class HelloWorldServiceField {

    public String helloFromService() {
        return "Hello World from service Field Injection";
    }

}
