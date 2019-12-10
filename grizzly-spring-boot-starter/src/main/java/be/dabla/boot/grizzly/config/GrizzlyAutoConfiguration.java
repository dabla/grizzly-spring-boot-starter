package be.dabla.boot.grizzly.config;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import be.dabla.boot.grizzly.server.GrizzlyServletWebServerFactory;
import javax.inject.Inject;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@ConditionalOnClass(HttpServer.class)
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
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
        return new GrizzlyServletWebServerFactory(resourceConfig.property("contextConfig", context));
    }

    @Bean
    public ResourceConfigCustomizer grizzlyResourceConfigCustomizer() {
        return new GrizzlyResourceConfigCustomizer(context);
    }
}
