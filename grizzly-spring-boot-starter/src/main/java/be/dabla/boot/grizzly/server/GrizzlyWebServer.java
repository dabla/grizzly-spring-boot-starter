package be.dabla.boot.grizzly.server;

import java.io.Closeable;
import java.io.IOException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

class GrizzlyWebServer implements WebServer, Closeable {
    private final HttpServer delegate;

    GrizzlyWebServer(HttpServer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start() throws WebServerException {
        try {
            delegate.start();
        } catch (Exception e) {
            throw new WebServerException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws WebServerException {
        delegate.stop();
    }

    @Override
    public int getPort() {
        return delegate.getListener("grizzly").getPort();
    }

    @Override
    public void close() throws IOException {
        stop();
    }
}
