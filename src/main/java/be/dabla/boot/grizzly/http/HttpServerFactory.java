package be.dabla.boot.grizzly.http;

import be.dabla.boot.grizzly.config.GrizzlyProperties;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import static be.dabla.boot.grizzly.http.HttpServerBuilder.aHttpServer;
import static java.lang.System.getProperty;
import static java.lang.Thread.currentThread;
import static java.util.Collections.list;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static javax.servlet.jsp.JspFactory.setDefaultFactory;
import static org.apache.jasper.Constants.SERVLET_CLASSPATH;
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
                    .withPort(getProperty("server.port", String.valueOf(properties.getHttp().getPort())))
                    .withPath(properties.getHttp().getContextPath())
                    .withResourceConfig(resourceConfig)
                    .withCompressionMode(properties.getHttp().getCompressionMode())
                    .withCompressableMimeTypes(properties.getHttp().getCompressableMimeTypes())
                    .withCompressionMinSize(properties.getHttp().getMinimumCompressionSize().toBytes())
                    .build();

            addHttpHandler(httpServer);
            addJspServlet();
            addServlets(initializers);
            webappContext.deploy(httpServer);
            return httpServer;
        } catch (URISyntaxException | IOException e) {
           throw new RuntimeException(e);
        }
    }

    private static String getJspServletClassPath() throws IOException {
        return concat(of(getProperty("java.class.path")), list(currentThread().getContextClassLoader().getResources("/")).stream().map(URL::getPath)).collect(joining(":"));
    }

    private static Function<String,String> toClassPath() {
        return path -> {
            int index = path.indexOf("!");

            if (index > -1) {
                return path.substring(index + 1).replace("!/","");
            }

            return path;
        };
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

    private void addJspServlet() throws IOException {
        if (properties.getJsp().isRegistered()) {
            setDefaultFactory(new JspFactoryImpl());
            webappContext.addServlet("JSPContainer", properties.getJsp().getServlet())
                         .addMapping(properties.getJsp().getUrlMapping());
            webappContext.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
            webappContext.setAttribute(SERVLET_CLASSPATH, getProperty("java.class.path"));
        }
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
