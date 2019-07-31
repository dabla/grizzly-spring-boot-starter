package be.dabla.boot.grizzly.http;

import be.dabla.boot.grizzly.config.GrizzlyProperties;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.apache.jasper.servlet.JspServlet;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import static be.dabla.boot.grizzly.http.HttpServerBuilder.aHttpServer;
import static java.lang.System.getProperty;
import static java.util.stream.Stream.of;
import static org.glassfish.grizzly.http.server.HttpHandlerRegistration.builder;
import static org.slf4j.LoggerFactory.getLogger;

public class HttpServerFactory {
    private static final Logger LOGGER = getLogger(HttpServerFactory.class);

    @Inject
    private GrizzlyProperties properties;
    @Inject
    private WebappContext webappContext;

    public HttpServer create(ResourceConfig resourceConfig, ServletContextInitializer... initializers) {
        try {
            HttpServer httpServer = aHttpServer()
                    .withScheme(properties.getHttp().getScheme())
                    .withHost(properties.getHttp().getHost())
                    .withPort(getProperty("http.webserver.port", String.valueOf(properties.getHttp().getPort())))
                    .withPath(properties.getHttp().getContextPath() + properties.getHttp().getPath())
                    .withResourceConfig(resourceConfig)
                    .withCompressionMode(properties.getHttp().getCompressionMode())
                    .withCompressableMimeTypes(properties.getHttp().getCompressableMimeTypes())
                    .withCompressionMinSize(properties.getHttp().getMinimumCompressionSize())
                    .build();

            addHttpHandler(httpServer);
            addJspServlet();
            addServlets(initializers);
            webappContext.deploy(httpServer);
            return httpServer;
        } catch (URISyntaxException e) {
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

    private void addJspServlet() {
        ServletRegistration registration = webappContext.addServlet("JSPContainer", JspServlet.class);
        registration.addMapping(properties.getJsp().getUrlMapping());
    }

    private void addHttpHandler(HttpServer httpServer) {
        CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(getClass().getClassLoader(), properties.getHttp().getDocRoot());
        httpHandler.setFileCacheEnabled(false); // Disable cache because it's very very slow

        for (String urlMapping : properties.getHttp().getUrlMapping()) {
            HttpHandlerRegistration mapping = builder().contextPath(properties.getHttp().getContextPath())
                                                       .urlPattern(urlMapping)
                                                       .build();
            httpServer.getServerConfiguration()
                      .addHttpHandler(httpHandler, mapping);
        }
    }
}
