package org.springframework.boot.grizzly.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.slf4j.Logger;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

import java.io.Closeable;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

class GrizzlyWebServer implements WebServer, Closeable {
    private static final Logger LOGGER = getLogger(GrizzlyWebServer.class);

    private final HttpServer delegate;

    GrizzlyWebServer(HttpServer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start() throws WebServerException {
        try {
            delegate.start();
            //LOGGER.info("Grizzly web server started at {}.", getURI(delegate).toURL());
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

    /*private static URI getURI(HttpServer httpServer) throws URISyntaxException {
        NetworkListener networkListener = httpServer.getListener("grizzly");
        return new URIBuilder()
                .setScheme(DEFAULT_SCHEME)
                .setHost(networkListener.getHost())
                .setPort(networkListener.getPort())
                .setPath(PATH)
                .build();
    }*/
}
