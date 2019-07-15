package org.springframework.boot.grizzly.config;

import java.util.Map.Entry;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ParamConverter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.grizzly.http.HttpServerFactory;
import org.springframework.boot.grizzly.server.GrizzlyServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@ConditionalOnClass(HttpServer.class)
public class GrizzlyAutoConfiguration {
    private static final Logger LOGGER = getLogger(GrizzlyAutoConfiguration.class);
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
        return new GrizzlyServletWebServerFactory(httpServerFactory, configure(resourceConfig.orElse(DEFAULT_RESOURCE_CONFIG)));
    }

    private ResourceConfig configure(ResourceConfig resourceConfig) {
        for (Entry<String,ParamConverter> paramConverter : context.getBeansOfType(ParamConverter.class).entrySet()) {
            if (!resourceConfig.isRegistered(paramConverter.getValue())) {
                resourceConfig.register(paramConverter.getValue());
                LOGGER.info("Registered parameter converter named '{}'", paramConverter.getKey());
            }
        }

        for (Entry<String,ExceptionMapper> exceptionMapper : context.getBeansOfType(ExceptionMapper.class).entrySet()) {
            if (!resourceConfig.isRegistered(exceptionMapper.getValue())) {
                resourceConfig.register(exceptionMapper.getValue());
                LOGGER.info("Registered exception mapper named '{}'", exceptionMapper.getKey());
            }
        }

        for (Entry<String,Object> resource : context.getBeansWithAnnotation(Path.class).entrySet()) {
            if (!resourceConfig.isRegistered(resource.getValue().getClass())) {
                resourceConfig.register(resource.getValue().getClass());
                LOGGER.info("Registered resource named '{}'", resource.getKey());
            }
        }

        return resourceConfig.property("contextConfig", context);
    }
}
