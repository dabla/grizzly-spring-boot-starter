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
    public ResourceConfigCustomizer resourceConfigCustomizer() {
        return new ResourceConfigCustomizer() {
            @Override
            public void customize(ResourceConfig resourceConfig) {
                Map<String,Object> properties = new HashMap<>();
                properties.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
                properties.put("com.sun.jersey.config.feature.trace", "ALL");
                resourceConfig.addProperties(properties);
            }
        };
    }
}
```

## Properties

It's possible to override the [default properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) listed here below.

| Parameter | Default value | Description |
| --- | --- | --- |
| grizzly.http.scheme                     | http | http / https |
| grizzly.http.doc-root                   | / | Physical location where grizzly will find it's contents (e.g. *.html or *.jsp files). |
| grizzly.http.url-mapping                | / | |
| grizzly.jsp.url-mapping                 | /*.jsp | |
| server.address                          | 0.0.0.0 | The network host to which the grizzly network listener will bind. If not user specified, it will bind to 0.0.0.0 (default value). |
| server.port                             | 8080 | The network port to which the grizzly network will bind. If not user specified, it will bind to port 8080 (default value). |
| server.compression.enabled              | false | By default compression mode is disabled. |
| server.compression.mime-types           | application/json,<br/>application/xml,<br/>text/javascript,<br/>text/plain,<br/>text/htm | |
| server.compression.min-response-size    | 2KB | |
| server.servlet.context-path             | / | Context path is part of the URI on which the application handler will be deployed. |
| server.servlet.application-display-name | application | Display name of the application. |
| server.servlet.jsp.class-name           | org.apache.jasper.servlet.JspServlet | Class name of the servlet to use for JSPs. |
| server.servlet.jsp.registered           | true | Whether the JSP servlet has to be registered or not. |

Prior to version 1.2, non standard Spring Boot properties where used to define port and address as shown in mapping table below:

| Parameter name until 1.2 | Parameter name since 1.2 compliant with Spring Boot properties |
| --- | --- |
| grizzly.http.port | server.port |
| grizzly.http.host | server.address |
| grizzly.http.context-path | server.servlet.context-path |
| grizzly.http.compression-mode | server.compression.enabled |
| grizzly.http.compressable-mime-types | server.compression.mime-types |
| grizzly.http.minimum-compression-size | server.compression.min-response-size |

The URI on which the grizzly application handler will be deployed will be constructed as follow given the above parameters:

${grizzly.http.scheme}://${server.address}:${grizzly.http.port}/${server.servlet.context-path}

The above would have given following URI with default parameters: http://0.0.0.0:8080/

For more information regarding the grizzly properties, please go the the [official documentation page](https://javaee.github.io/grizzly/httpserverframework.html).

## How to use in your project

Example for Maven:
```xml
<dependency>
    <groupId>be.dabla</groupId>
    <artifactId>grizzly-spring-boot-starter</artifactId>
    <version>2.7</version>
</dependency>
<!-- Add following dependency to add JSP support in Grizzly -->
<dependency>
    <groupId>be.dabla</groupId>
    <artifactId>grizzly-spring-boot-jasper</artifactId>
    <version>2.7</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.4.5</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
     </exclusions>
</dependency>
```

Maven artifact can be found on [Maven Central](https://mvnrepository.com/artifact/be.dabla/grizzly-spring-boot-starter).

## Using Spring REST controllers instead of Jersey (JAX-RS)

Since version 2.6 it is possible to use the Spring REST controllers instead of the default Jersey JAX-RS implementation.
To be able to do so, make sure you specify the spring.jersey.application-path property to a specific path which is
different than the root (e.g. /) otherwise there will be a path conflict and requests will never be able to reach the
Spring DispatcherServlet.

An example:

```
spring.jersey.application-path=/api
```

## Migration to Grizzly 3.0.0

We're now awaiting for Spring Boot to migrate to the new jakarta serlvet api,
which requires the usage of the new package names (e.g. jakarta.servlet instead of javax.servlet).

Once [Spring Boot supports Jakarta EE9](https://github.com/spring-projects/spring-framework/issues/25354), we will upgrade to Grizzly 3.x.x.

A first milestone of [Spring Boot 3.0.0](https://spring.io/blog/2022/01/20/spring-boot-3-0-0-m1-is-now-available) has been released.
