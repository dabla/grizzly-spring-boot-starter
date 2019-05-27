package org.springframework.boot.grizzly.server;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.grizzly.http.HttpServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

public class GrizzlyServletWebServerFactory implements ServletWebServerFactory {
    private final HttpServerFactory httpServerFactory;
    private final ResourceConfig resourceConfig;

    public GrizzlyServletWebServerFactory(HttpServerFactory httpServerFactory, ResourceConfig resourceConfig) {
        this.httpServerFactory = httpServerFactory;
        this.resourceConfig = resourceConfig;
    }

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        return new GrizzlyWebServer(httpServerFactory.create(resourceConfig));
    }
}
