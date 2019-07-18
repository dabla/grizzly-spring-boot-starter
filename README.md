# Spring-Boot-Starter-Grizzly
[![Build Status](https://travis-ci.org/dabla/spring-boot-starter-grizzly.svg?branch=master)](https://travis-ci.org/dabla/spring-boot-starter-grizzly)

Spring Boot Starter module which easily allows you to use Grizzly as an alternative web container with JSP support.  REST services exposed as Spring beans will be automatically registered.

## Code sample
```java
package org.springframework.boot.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class GrizzlyApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GrizzlyApplication.class, args);
    }

    @Bean
    public ResourceConfig resourceConfig() {
        return new ResourceConfig();
    }
}
```

## Properties

It's possible to override the default properties listed here below.

| Parameter | Default value | Description |
| --- | --- | --- |
| grizzly.http.scheme                 | http | http / https |
| grizzly.http.host                   | 0.0.0.0 | The network host to which the grizzly network listener will bind. If not user specified, it will bind to 0.0.0.0 (default value). |
| grizzly.http.port                   | 8080 | The network port to which the grizzly network will bind. If not user specified, it will bind to port 8080 (default value). |
| grizzly.http.contextPath            | / | Context path is part of the URI on which the application handler will be deployed. |
| grizzly.http.path                   | / | Path is part of the URI on which the application handler will be deployed. |
| grizzly.http.compressionMode        | OFF | By default compression mode is disabled. |
| grizzly.http.compressableMimeTypes  | application/json, | |
|                                     | application/xml, | |
|                                     | text/javascript, | |
|                                     | text/plain, | |
|                                     | text/htm | |
| grizzly.http.minimumCompressionSize | 10240 | |
| grizzly.http.docRoot                | / | Physical location where grizzly will find it's contents (e.g. *.html or *.jsp files). |
| grizzly.http.urlMapping             | / | |
| grizzly.jsp.urlMapping              | /*.jsp | |

The URI on which the grizzly application handler will be deployed will be constructed as follow given the above parameters:

[grizzly.http.scheme]://[grizzly.http.host]:[grizzly.http.port]/[grizzly.http.contextPath]/[grizzly.http.path] (http://0.0.0.0:8080/)

For more information regarding the grizzly properties, please go the the [official documentation page](https://javaee.github.io/grizzly/httpserverframework.html).

## How to use in your project

Example for Maven:
```xml
<dependency>
    <groupId>be.dabla</groupId>
    <artifactId>spring-boot-starter-grizzly</artifactId>
    <version>1.0</version>
</dependency>
```
Maven artifact can be found at [https://oss.sonatype.org/content/repositories/public/be/dabla/spring-boot-starter-grizzly](https://oss.sonatype.org/content/repositories/public/be/dabla/spring-boot-starter-grizzly)
