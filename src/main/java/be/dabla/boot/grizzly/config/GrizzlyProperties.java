package be.dabla.boot.grizzly.config;

import org.glassfish.grizzly.http.CompressionConfig.CompressionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.glassfish.grizzly.http.CompressionConfig.CompressionMode.OFF;

@ConfigurationProperties(prefix = "grizzly")
public class GrizzlyProperties {
    public static final int DEFAULT_COMPRESSION_MIN_SIZE = 10 * 1024;
    public static final String TEXT_JAVASCRIPT = "text/javascript";

    private final Http http = new Http();
    private final Jsp jsp = new Jsp();

    public static class Http {
        /**
         * http / https
         */
        private String scheme = "http";
        /**
         * The network host to which the grizzly network listener will bind. If not user specified, it will bind to 0.0.0.0 (default value).
         */
        private String host = "0.0.0.0";
        /**
         * The network port to which the grizzly network will bind. If not user specified, it will bind to port 8080 (default value).
         */
        private int port = 8080;
        /**
         * Context path is part of the URI on which the application handler will be deployed.
         */
        private String contextPath = "";
        /**
         * Path is part of the URI on which the application handler will be deployed.
         */
        private String path = "/";
        /**
         * By default compression mode is disabled.
         */
        private CompressionMode compressionMode = OFF;
        private String[] compressableMimeTypes = new String[] { APPLICATION_JSON, APPLICATION_XML, TEXT_JAVASCRIPT, TEXT_PLAIN, TEXT_HTML };
        private int minimumCompressionSize = DEFAULT_COMPRESSION_MIN_SIZE;
        /**
         * Physical location where grizzly will find it's contents (e.g. *.html or *.jsp files).
         */
        private String[] docRoot = new String[] { "/" };
        private String[] urlMapping = new String[] { "/" };

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getContextPath() {
            return contextPath;
        }

        public void setContextPath(String contextPath) {
            this.contextPath = contextPath;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public CompressionMode getCompressionMode() {
            return compressionMode;
        }

        public void setCompressionMode(CompressionMode compressionMode) {
            this.compressionMode = compressionMode;
        }

        public String[] getCompressableMimeTypes() {
            return compressableMimeTypes;
        }

        public void setCompressableMimeTypes(String... compressableMimeTypes) {
            this.compressableMimeTypes = compressableMimeTypes;
        }

        public int getMinimumCompressionSize() {
            return minimumCompressionSize;
        }

        public void setMinimumCompressionSize(int minimumCompressionSize) {
            this.minimumCompressionSize = minimumCompressionSize;
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

    public static class Jsp {
        private String[] urlMapping = new String[] { "/*.jsp" };
        private String[] listeners = new String[0];
        private String[] filters = new String[0];

        public String[] getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(String... urlMapping) {
            this.urlMapping = urlMapping;
        }

        public String[] getListeners() {
            return listeners;
        }

        public void setListeners(String[] listeners) {
            this.listeners = listeners;
        }

        public String[] getFilters() {
            return filters;
        }

        public void setFilters(String[] filters) {
            this.filters = filters;
        }
    }

    public Http getHttp() {
        return http;
    }

    public Jsp getJsp() {
        return jsp;
    }
}
