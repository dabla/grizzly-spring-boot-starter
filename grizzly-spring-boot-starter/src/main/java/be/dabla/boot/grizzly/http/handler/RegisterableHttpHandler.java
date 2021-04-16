package be.dabla.boot.grizzly.http.handler;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;

public class RegisterableHttpHandler {
    private final HttpHandler httpHandler;
    private final HttpHandlerRegistration[] registrations;

    public RegisterableHttpHandler(HttpHandler httpHandler, HttpHandlerRegistration[] registrations) {
        this.httpHandler = httpHandler;
        this.registrations = registrations;
    }

    public static RegisterableHttpHandler registerableHttpHandler(final ServletHttpHandler servletHttpHandler) {
        return new RegisterableHttpHandler(servletHttpHandler, null) {
            @Override
            public HttpHandlerRegistration[] getRegistrations() {
                return servletHttpHandler.getRegistrations();
            }
        };
    }

    public HttpHandler getHttpHandler() {
        return httpHandler;
    }

    public HttpHandlerRegistration[] getRegistrations() {
        return registrations;
    }

    public void register(HttpServer httpServer) {
        httpServer.getServerConfiguration().addHttpHandler(getHttpHandler(), getRegistrations());
    }
}
