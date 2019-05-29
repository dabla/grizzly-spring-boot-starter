package org.springframework.boot.grizzly.config;

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
        private String scheme = "http";
        private String host = "0.0.0.0";
        private int port = 8080;
        private String path = "/";
        private CompressionMode compressionMode = OFF;
        private String[] compressableMimeTypes = new String[] { APPLICATION_JSON, APPLICATION_XML, TEXT_JAVASCRIPT, TEXT_PLAIN, TEXT_HTML };
        private int minimumCompressionSize = DEFAULT_COMPRESSION_MIN_SIZE;
        private String docRoot = "/";
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

        public String getDocRoot() {
            return docRoot;
        }

        public void setDocRoot(String docRoot) {
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
