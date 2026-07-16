package com.shree.started.hello;

import io.micronaut.context.BeanProvider;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Collection;
import java.util.List;

@Controller("/hello")
public class HelloWorldController {

    @Inject
    private HelloWorldServiceField helloWorldServiceField;

    private final HelloWorldServiceConstructor helloWorldServiceConstructor;

    private final IHelloWorldServiceInterface helloWorldServiceInterface;

    private final IHelloWorldServiceInterface helloWorldServiceInterfaceSecondary;

    private final Collection<IHelloWorldServiceInterface> iHelloWorldServiceInterfaces;

    private final BeanProvider<IHelloWorldServiceInterface> iHelloWorldServiceInterfaceBeanProvider;

    private final IHelloWorldServiceInterface helloWorldServiceInterfacePrimaryQualifier;

    private final IHelloWorldServiceInterface helloWorldServiceInterfaceSecondaryQualifier;

    public HelloWorldController(HelloWorldServiceConstructor helloWorldServiceConstructor, IHelloWorldServiceInterface helloWorldServiceInterface, @Named("Secondary") IHelloWorldServiceInterface helloWorldServiceInterfaceSecondary,
                                Collection<IHelloWorldServiceInterface> iHelloWorldServiceInterfaces,
                                BeanProvider<IHelloWorldServiceInterface> iHelloWorldServiceInterfaceBeanProvider,
                                @HelloInterfacePrimaryQualifier IHelloWorldServiceInterface helloWorldServiceInterfacePrimaryQualifier,
                                @HelloInterfaceSecondaryQualifier IHelloWorldServiceInterface helloWorldServiceInterfaceSecondaryQualifier) {
        this.helloWorldServiceConstructor = helloWorldServiceConstructor;
        this.helloWorldServiceInterface = helloWorldServiceInterface;
        this.helloWorldServiceInterfaceSecondary = helloWorldServiceInterfaceSecondary;
        this.iHelloWorldServiceInterfaces = List.copyOf(iHelloWorldServiceInterfaces);
        this.iHelloWorldServiceInterfaceBeanProvider = iHelloWorldServiceInterfaceBeanProvider;
        this.helloWorldServiceInterfacePrimaryQualifier = helloWorldServiceInterfacePrimaryQualifier;
        this.helloWorldServiceInterfaceSecondaryQualifier = helloWorldServiceInterfaceSecondaryQualifier;
    }

    @Get(produces = MediaType.TEXT_PLAIN)
    public MutableHttpResponse<Object> index() {
        return HttpResponse.status(HttpStatus.OK).body("Hello World");
    }

    @Get(uri = "/json", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSON() {
        HelloRecord helloRecord = new HelloRecord("Hello World from Dockerized Micronaut App");
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/field", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONFieldInject() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceField.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/constructor", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONConstructorInject() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceConstructor.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/interface", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONInterfaceInjectPrimary() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceInterface.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/interface/secondary", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONInterfaceInjectSecondary() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceInterfaceSecondary.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/interface/collection", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecordCollection> indexJSONInterfaceInjectCollection() {
        List<String> msgs = iHelloWorldServiceInterfaces.stream().map(IHelloWorldServiceInterface::helloFromService).toList();
        HelloRecordCollection helloRecordCollection = new HelloRecordCollection(msgs);
        return HttpResponse.status(HttpStatus.OK).body(helloRecordCollection);
    }

    @Get(uri = "/json/interface/bean", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecordBean> indexJSONInterfaceInjectBean() {
        //provider.get() will return the primary bean, which is HelloWorldServiceInterfaceInterfacePrimary
        final List<String> msgs = iHelloWorldServiceInterfaceBeanProvider.stream().map(IHelloWorldServiceInterface::helloFromService).toList();

        HelloRecordBean helloRecordBean = new HelloRecordBean("Bean", msgs);
        return HttpResponse.status(HttpStatus.OK).body(helloRecordBean);
    }

    @Get(uri = "/json/interface/qprimary", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONInterfaceInjectPrimaryQualifier() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceInterfacePrimaryQualifier.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

    @Get(uri = "/json/interface/qsecondary", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<HelloRecord> indexJSONInterfaceInjectSecondaryQualifier() {
        HelloRecord helloRecord = new HelloRecord(helloWorldServiceInterfaceSecondaryQualifier.helloFromService());
        return HttpResponse.status(HttpStatus.OK).body(helloRecord);
    }

}
