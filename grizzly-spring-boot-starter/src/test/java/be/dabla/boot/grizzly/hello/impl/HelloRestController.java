package be.dabla.boot.grizzly.hello.impl;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRestController {
    String value;

    public HelloRestController() {
        value = null;
    }

    @GetMapping("/grizzly/index")
    public String hello() {
        return "Hello world!";
    }
}
