package be.dabla.boot.grizzly.config;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import be.dabla.boot.grizzly.http.handler.RegisterableHttpHandler;
import be.dabla.boot.grizzly.http.handler.ServletHttpHandler;
import be.dabla.boot.grizzly.server.GrizzlyServletWebServerFactory;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
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
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.glassfish.grizzly.http.server.HttpHandlerRegistration.builder;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.util.UrlPathHelper.PATH_ATTRIBUTE;

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
    public HttpServerFactory httpServerFactory(GrizzlyProperties properties,
                                               WebappContext webappContext,
                                               Collection<ServletRegistrationBean> servletRegistrations,
                                               Collection<RegisterableHttpHandler> registeredHttpHandlers) {
        Collection<RegisterableHttpHandler> httpHandlers = concat(servletRegistrations
                .stream()
                .sorted(new OrderComparator())
                .map(servletRegistration -> new ServletHttpHandler(webappContext, servletRegistration))
                .map(RegisterableHttpHandler::registerableHttpHandler), registeredHttpHandlers.stream())
                .collect(toList());
        return new HttpServerFactory(properties, webappContext, httpHandlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceConfig resourceConfig() {
        return new ResourceConfig().property("contextConfig", context);
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
    public GrizzlyServletWebServerFactory grizzlyServletWebServerFactory() {
        return new GrizzlyServletWebServerFactory();
    }

    @Bean
    public ResourceConfigCustomizer grizzlyResourceConfigCustomizer() {
        return new GrizzlyResourceConfigCustomizer(context);
    }

    @Bean
    public RequestMappingHandlerMapping grizzlyRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            @Override
            protected String initLookupPath(HttpServletRequest request) {
                String lookupPath = getUrlPathHelper().getRequestUri(request);
                request.setAttribute(PATH_ATTRIBUTE, lookupPath);
                return lookupPath;
            }

            public int getOrder() {
                return 0;
            }
        };
    }

    @Bean
    public RegisterableHttpHandler staticHttpHandler(GrizzlyProperties properties) {
        CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(getClass().getClassLoader(), properties.getHttp().getDocRoot());
        httpHandler.setFileCacheEnabled(false); // Disable cache because it's very very slow

        return new RegisterableHttpHandler(httpHandler, asList(properties.getHttp().getUrlMapping())
                .stream()
                .map(mapping -> builder().contextPath(properties.getHttp().getContextPath()).urlPattern(mapping).build())
                .toArray(HttpHandlerRegistration[]::new));
    }
}
