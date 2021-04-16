package be.dabla.boot.grizzly.http;

import be.dabla.boot.grizzly.config.GrizzlyProperties;
import be.dabla.boot.grizzly.http.handler.RegisterableHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.function.Consumer;

import static be.dabla.boot.grizzly.http.HttpServerBuilder.aHttpServer;
import static java.lang.System.getProperty;
import static java.util.stream.Stream.of;
import static org.slf4j.LoggerFactory.getLogger;

public class HttpServerFactory {
    private static final Logger LOGGER = getLogger(HttpServerFactory.class);

    private final GrizzlyProperties properties;
    private final WebappContext webappContext;
    private final Collection<RegisterableHttpHandler> registeredHttpHandlers;

    public HttpServerFactory(GrizzlyProperties properties, WebappContext webappContext, Collection<RegisterableHttpHandler> registeredHttpHandlers) {
        this.properties = properties;
        this.webappContext = webappContext;
        this.registeredHttpHandlers = registeredHttpHandlers;
    }

    public HttpServer create(ServletContextInitializer... initializers) {
        try {
            HttpServer httpServer = aHttpServer()
                    .withScheme(properties.getHttp().getScheme())
                    .withHost(properties.getHttp().getHost())
                    .withPort(getProperty("server.port", String.valueOf(properties.getHttp().getPort())))
                    .withPath(properties.getHttp().getContextPath())
                    .withCompressionMode(properties.getHttp().getCompressionMode())
                    .withCompressableMimeTypes(properties.getHttp().getCompressableMimeTypes())
                    .withCompressionMinSize(properties.getHttp().getMinimumCompressionSize().toBytes())
                    .build();

            addServlets(initializers);
            registeredHttpHandlers.forEach(registeredHttpHandler -> registeredHttpHandler.register(httpServer));
            webappContext.deploy(httpServer);
            return httpServer;
        } catch (URISyntaxException | IOException e) {
           throw new RuntimeException(e);
        }
    }

    private void addServlets(ServletContextInitializer[] initializers) {
        of(initializers).forEach(onStartup(webappContext));
    }

    private static Consumer<ServletContextInitializer> onStartup(WebappContext webappContext) {
        return initializer -> {
            try {
                initializer.onStartup(webappContext);
            } catch (ServletException e) {
                throw new IllegalStateException(e);
            } catch (UnsupportedOperationException e) {
                LOGGER.warn("{}: {}", e.getClass().getName(), e.getMessage());
            }
        };
    }
}
