package be.dabla.boot.grizzly.config;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.ws.rs.Path;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.context.ApplicationContext;

import static org.slf4j.LoggerFactory.getLogger;

class GrizzlyResourceConfigCustomizer implements ResourceConfigCustomizer {
    private static final Logger LOGGER = getLogger(GrizzlyResourceConfigCustomizer.class);

    private final ApplicationContext context;

    GrizzlyResourceConfigCustomizer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void customize(ResourceConfig resourceConfig) {
        registerBeansOfType(resourceConfig, ParamConverterProvider.class);
        registerBeansOfType(resourceConfig, ParamConverter.class);
        registerBeansOfType(resourceConfig, ExceptionMapper.class);
        registerBeansOfType(resourceConfig, Feature.class);
        registerBeansOfType(resourceConfig, ContextResolver.class);
        registerBeansWithAnnotation(resourceConfig, Path.class);
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
