# Grizzly-Spring-Boot-Starter
[![Build Status](https://travis-ci.org/dabla/grizzly-spring-boot-starter.svg?branch=master)](https://travis-ci.org/dabla/grizzly-spring-boot-starter)

Spring Boot Starter module which easily allows you to use Grizzly as an alternative web container with JSP support.  REST services exposed as Spring beans will be automatically registered.

## Code sample
```java
package be.dabla.boot.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GrizzlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrizzlyApplication.class, args);
    }

    @Bean
    // Optional, but may be defined if for example additional properties like shown here below have to be passed.
    public ResourceConfig resourceConfig() {
        Map<String,Object> properties = new HashMap<>();
        properties.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        properties.put("com.sun.jersey.config.feature.trace", "ALL");
        return new ResourceConfig().addProperties(properties);
    }
}
```

## Properties

It's possible to override the default properties listed here below.

| Parameter | Default value | Description |
| --- | --- | --- |
| grizzly.http.scheme                  | http | http / https |
| grizzly.http.host                    | 0.0.0.0 | The network host to which the grizzly network listener will bind. If not user specified, it will bind to 0.0.0.0 (default value). |
| grizzly.http.port                    | 8080 | The network port to which the grizzly network will bind. If not user specified, it will bind to port 8080 (default value). |
| grizzly.http.context-path            | / | Context path is part of the URI on which the application handler will be deployed. |
| grizzly.http.path                    | / | Path is part of the URI on which the application handler will be deployed. |
| grizzly.http.compression-mode        | OFF | By default compression mode is disabled. |
| grizzly.http.compressable-mime-types | application/json, | |
|                                      | application/xml, | |
|                                      | text/javascript, | |
|                                      | text/plain, | |
|                                      | text/htm | |
| grizzly.http.minimum-compression-size| 10240 | |
| grizzly.http.doc-root                | / | Physical location where grizzly will find it's contents (e.g. *.html or *.jsp files). |
| grizzly.http.url-mapping             | / | |
| grizzly.jsp.url-mapping              | /*.jsp | |

The URI on which the grizzly application handler will be deployed will be constructed as follow given the above parameters:

[grizzly.http.scheme]://[grizzly.http.host]:[grizzly.http.port]/[grizzly.http.contextPath]/[grizzly.http.path] (http://0.0.0.0:8080/)

For more information regarding the grizzly properties, please go the the [official documentation page](https://javaee.github.io/grizzly/httpserverframework.html).

## How to use in your project

Example for Maven:
```xml
<dependency>
    <groupId>be.dabla</groupId>
    <artifactId>grizzly-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```
Maven artifact can be found on Maven Central.
