# Spring-Boot-Starter-Grizzly
[![Build Status](https://travis-ci.org/dabla/spring-boot-starter-grizzly.svg?branch=master)](https://travis-ci.org/dabla/spring-boot-starter-grizzly)

## Code sample
```java
package org.springframework.boot.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class GrizzlyApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GrizzlyApplication.class, args);
    }

    @Bean
    public ResourceConfig resourceConfig() {
        return new ResourceConfig();
    }

	@Override
	public void run(String... args) throws Exception {

	}
}

```

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
