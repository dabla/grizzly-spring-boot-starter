package org.springframework.boot.grizzly.config;

import java.util.Optional;
import javax.inject.Inject;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.grizzly.http.HttpServerFactory;
import org.springframework.boot.grizzly.server.GrizzlyServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(HttpServer.class)
public class GrizzlyAutoConfiguration {
    private static final ResourceConfig DEFAULT_RESOURCE_CONFIG = new ResourceConfig();

    @Inject
    private ApplicationContext context;

    @Bean
    @ConditionalOnMissingBean
    public static GrizzlyProperties grizzlyProperties() {
        return new GrizzlyProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpServerFactory httpServerFactory() {
        return new HttpServerFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public GrizzlyServletWebServerFactory grizzlyServletWebServerFactory(Optional<ResourceConfig> resourceConfig, HttpServerFactory httpServerFactory) {
        return new GrizzlyServletWebServerFactory(httpServerFactory, resourceConfig.orElse(DEFAULT_RESOURCE_CONFIG)
                                                                                   .property("contextConfig", context));
    }
}
