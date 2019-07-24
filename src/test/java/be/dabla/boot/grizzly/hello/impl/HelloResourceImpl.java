package be.dabla.boot.grizzly.hello.impl;

import be.dabla.boot.grizzly.hello.HelloResource;
import javax.inject.Named;

import static java.lang.String.format;

@Named
public class HelloResourceImpl implements HelloResource {
    @Override
    public String hello(String response) {
        return format("Hello %s!", response);
    }
}
