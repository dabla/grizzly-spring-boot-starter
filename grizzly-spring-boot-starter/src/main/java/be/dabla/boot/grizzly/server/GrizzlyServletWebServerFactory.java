package be.dabla.boot.grizzly.server;

import be.dabla.boot.grizzly.http.HttpServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

import javax.inject.Inject;

public class GrizzlyServletWebServerFactory implements ServletWebServerFactory {
    @Inject
    private HttpServerFactory httpServerFactory;

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        return new GrizzlyWebServer(httpServerFactory.create(initializers));
    }
}
