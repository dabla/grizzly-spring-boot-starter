package be.dabla.boot.grizzly.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.inject.Inject;
import org.glassfish.grizzly.http.CompressionConfig.CompressionMode;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import static java.util.Optional.ofNullable;
import static org.glassfish.grizzly.http.CompressionConfig.CompressionMode.OFF;
import static org.glassfish.grizzly.http.CompressionConfig.CompressionMode.ON;

@ConfigurationProperties(prefix = "grizzly")
public class GrizzlyProperties {
    @Inject
    private ServerProperties serverProperties;

    private final Http http = new Http();
    private final Jsp jsp = new Jsp();

    public class Http {
        /**
         * http / https
         */
        private String scheme = "http";/**
         /**
         * The network port to which the grizzly network will bind. If not user specified, it will bind to port 8080 (default value).
         */
        private int port = 8080;
        /**
         * Physical location where grizzly will find it's contents (e.g. *.html or *.jsp files).
         */
        private String[] docRoot = new String[] { "/" };
        private String[] urlMapping = new String[] { "/*.*" };

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public int getPort() {
            Integer port = ofNullable(serverProperties.getPort()).orElse(-1);
            return port.intValue() == -1 ? this.port : port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() throws UnknownHostException {
            return ofNullable(serverProperties.getAddress()).orElse(InetAddress.getByName("0.0.0.0")).getHostAddress();
        }

        public String getContextPath() {
            return ofNullable(serverProperties.getServlet().getContextPath()).orElse("/");
        }

        public CompressionMode getCompressionMode() {
            return serverProperties.getCompression().getEnabled() ? ON : OFF;
        }

        public String[] getCompressableMimeTypes() {
            return serverProperties.getCompression().getMimeTypes();
        }

        public DataSize getMinimumCompressionSize() {
            return serverProperties.getCompression().getMinResponseSize();
        }

        public String[] getDocRoot() {
            return docRoot;
        }

        public void setDocRoot(String[] docRoot) {
            this.docRoot = docRoot;
        }

        public String[] getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(String[] urlMapping) {
            this.urlMapping = urlMapping;
        }
    }

    public class Jsp {
        private String[] urlMapping = new String[] { "/*.jsp" };

        public String[] getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(String... urlMapping) {
            this.urlMapping = urlMapping;
        }

        public boolean isRegistered() {
            return serverProperties.getServlet().getJsp().getRegistered();
        }

        public String getServlet() {
            return serverProperties.getServlet().getJsp().getClassName();
        }
    }

    public Http getHttp() {
        return http;
    }

    public Jsp getJsp() {
        return jsp;
    }
}
