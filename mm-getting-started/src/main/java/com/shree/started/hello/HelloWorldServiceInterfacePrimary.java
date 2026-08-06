package com.shree.started.hello;

import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

@Singleton
@Primary
@HelloInterfacePrimaryQualifier
public class HelloWorldServiceInterfacePrimary implements IHelloWorldServiceInterface {
    @Override
    public String helloFromService() {
        return "Hello World from service Interface Primary Injection";
    }
}
