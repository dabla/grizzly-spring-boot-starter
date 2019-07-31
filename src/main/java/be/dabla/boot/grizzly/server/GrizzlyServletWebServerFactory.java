package be.dabla.boot.grizzly.server;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

import static java.util.stream.Stream.of;
import static org.slf4j.LoggerFactory.getLogger;

public class GrizzlyServletWebServerFactory implements ServletWebServerFactory {
    private static final Logger LOGGER = getLogger(GrizzlyServletWebServerFactory.class);

    private final ResourceConfig resourceConfig;

    @Inject
    private HttpServerFactory httpServerFactory;
    @Inject
    private WebappContext context;

    public GrizzlyServletWebServerFactory(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        HttpServer httpServer = httpServerFactory.create(resourceConfig);
        of(initializers).forEach(onStartup(context));
        return new GrizzlyWebServer(httpServer);
    }

    private static Consumer<ServletContextInitializer> onStartup(WebappContext context) {
        return initializer -> {
            try {
                initializer.onStartup(context);
            } catch (ServletException e) {
                throw new IllegalStateException(e);
            } catch (UnsupportedOperationException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        };
    }
}
