package com.shree.started.hello;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Singleton
@Named("Secondary")
@HelloInterfaceSecondaryQualifier
public class HelloWorldServiceInterface implements IHelloWorldServiceInterface {
    @Override
    public String helloFromService() {
        return "Hello World from service Interface Injection";
    }
}
