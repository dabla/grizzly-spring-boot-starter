package org.springframework.boot.grizzly.http;

import org.apache.jasper.servlet.JspServlet;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.grizzly.config.GrizzlyProperties;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;
import static org.apache.jasper.Constants.SERVLET_CLASSPATH;
import static org.springframework.boot.grizzly.http.HttpServerBuilder.aHttpServer;

@Named
public class HttpServerFactory {
    @Inject
    private GrizzlyProperties properties;

    public HttpServer create(ResourceConfig resourceConfig) {
        try {
            HttpServer httpServer = aHttpServer()
                    .withScheme(properties.getHttp().getScheme())
                    .withHost(properties.getHttp().getHost())
                    .withPort(getProperty("http.webserver.port", String.valueOf(properties.getHttp().getPort())))
                    .withPath(properties.getHttp().getPath())
                    .withResourceConfig(resourceConfig)
                    .withCompressionMode(properties.getHttp().getCompressionMode())
                    .withCompressableMimeTypes(properties.getHttp().getCompressableMimeTypes())
                    .withCompressionMinSize(properties.getHttp().getMinimimCompressionSize())
                    .build();
            WebappContext context = new WebappContext("WebappContext");

            ServletRegistration jspRegistration = context.addServlet("JSPContainer", JspServlet.class);
            jspRegistration.addMapping(properties.getJsp().getUrlMapping());
            context.setAttribute(SERVLET_CLASSPATH, getProperty("java.class.path"));
            context.deploy(httpServer);

            return httpServer;
        } catch (URISyntaxException e) {
           throw new RuntimeException(e);
        }
    }
}
