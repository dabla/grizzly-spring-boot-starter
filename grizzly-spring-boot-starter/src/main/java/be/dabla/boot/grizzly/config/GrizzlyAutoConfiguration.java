package be.dabla.boot.grizzly.config;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import be.dabla.boot.grizzly.server.GrizzlyServletWebServerFactory;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@ConditionalOnClass(HttpServer.class)
public class GrizzlyAutoConfiguration {
    private static final Logger LOGGER = getLogger(GrizzlyAutoConfiguration.class);

    @Inject
    private ApplicationContext context;

    @Bean
    @ConditionalOnMissingBean
    public static ServerProperties serverProperties() {
        return new ServerProperties();
    }

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
    public ResourceConfig resourceConfig() {
        return new ResourceConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebappContext webappContext(ServerProperties properties) {
        LOGGER.info("Running with {} v{}", GrizzlyAutoConfiguration.class.getPackage().getSpecificationTitle(), GrizzlyAutoConfiguration.class.getPackage().getSpecificationVersion());
        WebappContext webappContext = new WebappContext(properties.getServlet().getApplicationDisplayName(),
                                                        properties.getServlet().getContextPath()) {
            @Override
            public ClassLoader getClassLoader() {
                return context.getClassLoader();
            }
        };
        return webappContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public GrizzlyServletWebServerFactory grizzlyServletWebServerFactory(ResourceConfig resourceConfig) {
        return new GrizzlyServletWebServerFactory(register(resourceConfig));
    }

    private ResourceConfig register(ResourceConfig resourceConfig) {
        registerBeansOfType(resourceConfig, ParamConverterProvider.class);
        registerBeansOfType(resourceConfig, ParamConverter.class);
        registerBeansOfType(resourceConfig, ExceptionMapper.class);
        registerBeansOfType(resourceConfig, Feature.class);
        registerBeansOfType(resourceConfig, ContextResolver.class);
        registerBeansWithAnnotation(resourceConfig, Path.class);

        return resourceConfig.property("contextConfig", context);
    }

    private void registerBeansOfType(ResourceConfig resourceConfig, Class<?> type) {
        for (Map.Entry<String,?> entry : context.getBeansOfType(type).entrySet()) {
            registerBeanOfType(resourceConfig, entry);
        }
    }

    private void registerBeansWithAnnotation(ResourceConfig resourceConfig, Class<? extends Annotation> annotation) {
        for (Map.Entry<String,Object> entry : context.getBeansWithAnnotation(annotation).entrySet()) {
            registerBeanOfType(resourceConfig, entry);
        }
    }

    private static void registerBeanOfType(ResourceConfig resourceConfig, Map.Entry<String,?> entry) {
        if (!resourceConfig.isRegistered(entry.getValue())) {
            resourceConfig.register(entry.getValue());
            LOGGER.info("Bean '{}' of type [{}] registered", entry.getKey(), entry.getValue().getClass());
        }
    }
}
