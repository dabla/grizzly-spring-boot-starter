package be.dabla.boot.grizzly.http;

import java.net.URISyntaxException;
import javax.inject.Inject;
import org.apache.jasper.servlet.JspServlet;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import be.dabla.boot.grizzly.config.GrizzlyProperties;

import static java.lang.System.getProperty;
import static org.apache.jasper.Constants.SERVLET_CLASSPATH;
import static be.dabla.boot.grizzly.http.HttpServerBuilder.aHttpServer;

public class HttpServerFactory {
    @Inject
    private GrizzlyProperties properties;

    public HttpServer create(ResourceConfig resourceConfig) {
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
            addJspServlet(httpServer);

            return httpServer;
        } catch (URISyntaxException e) {
           throw new RuntimeException(e);
        }
    }

    private void addJspServlet(HttpServer httpServer) {
        WebappContext context = new WebappContext("WebappContext", properties.getHttp().getContextPath());
        ServletRegistration jspRegistration = context.addServlet("JSPContainer", JspServlet.class);
        jspRegistration.addMapping(properties.getJsp().getUrlMapping());
        context.setAttribute(SERVLET_CLASSPATH, getProperty("java.class.path"));
        context.deploy(httpServer);
    }

    private void addHttpHandler(HttpServer httpServer) {
        CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(getClass().getClassLoader(), properties.getHttp().getDocRoot());
        httpHandler.setFileCacheEnabled(false); // Disable cache because it's very very slow
        httpServer.getServerConfiguration()
                  .addHttpHandler(httpHandler, properties.getHttp().getUrlMapping());
    }
}
