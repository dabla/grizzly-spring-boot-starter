package be.dabla.boot.grizzly.server;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import javax.inject.Inject;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

public class GrizzlyServletWebServerFactory implements ServletWebServerFactory {

    private final ResourceConfig resourceConfig;

    @Inject
    private HttpServerFactory httpServerFactory;

    public GrizzlyServletWebServerFactory(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        return new GrizzlyWebServer(httpServerFactory.create(resourceConfig));
    }
}
