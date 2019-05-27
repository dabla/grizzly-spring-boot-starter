package org.springframework.boot.grizzly.http;

import org.apache.http.client.utils.URIBuilder;
import org.glassfish.grizzly.http.CompressionConfig;
import org.glassfish.grizzly.http.CompressionConfig.CompressionMode;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.glassfish.grizzly.http.CompressionConfig.CompressionMode.OFF;
import static org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer;
import static org.springframework.boot.grizzly.config.GrizzlyProperties.DEFAULT_COMPRESSION_MIN_SIZE;

public class HttpServerBuilder {
	private final URIBuilder uriBuilder;
	
	private HttpServerBuilder(URIBuilder uriBuilder) {
		this.uriBuilder = uriBuilder;
	}
	
	public static HttpServerBuilder aHttpServer() {
		return new HttpServerBuilder(new URIBuilder());
	}
	
	public HttpServerBuilder withScheme(String scheme) {
		this.uriBuilder.setScheme(scheme);
		return this;
	}
	
	public HttpServerBuilder withHost(String host) {
		this.uriBuilder.setHost(host);
		return this;
	}
	
	public HttpServerBuilder withPort(int port) {
		this.uriBuilder.setPort(port);
		return this;
	}
	
	public HttpServerBuilder withPort(String port) {
		if (port != null) {
			return withPort(parseInt(port));
		}
		
		return this;
	}
	
	public HttpServerBuilder withPath(String path) {
		this.uriBuilder.setPath(path);
		return this;
	}
	
	public BuildableHttpServer withResourceConfig(ResourceConfig resourceConfig) throws URISyntaxException {
		return new BuildableHttpServer(uriBuilder.build(),
									   resourceConfig);
	}
	
	public static class BuildableHttpServer {
		private final URI uri;
		private final ResourceConfig resourceConfig;
		private CompressionMode compressionMode = OFF;
		private int compressionMinSize = DEFAULT_COMPRESSION_MIN_SIZE;
		private Set<String> compressableMimeTypes = emptySet();

		private BuildableHttpServer(URI uri, ResourceConfig resourceConfig) {
			this.uri = uri;
			this.resourceConfig = resourceConfig;
		}
		
		public BuildableHttpServer withCompressionMode(CompressionMode compressionMode) {
			this.compressionMode = compressionMode;
			return this;
		}
		
		public BuildableHttpServer withCompressionMinSize(int compressionMinSize) {
			this.compressionMinSize = compressionMinSize;
			return this;
		}
		
		public BuildableHttpServer withCompressableMimeTypes(String... compressableMimeTypes) {
			return withCompressableMimeTypes(new HashSet(asList(compressableMimeTypes)));
		}

		public BuildableHttpServer withCompressableMimeTypes(Set<String> compressableMimeTypes) {
			this.compressableMimeTypes = unmodifiableSet(compressableMimeTypes);
			return this;
		}

		public HttpServer build() {
	        final HttpServer httpServer = createHttpServer(uri, resourceConfig, false);

	        getRuntime().addShutdownHook(new Thread(new Runnable() {
	            @Override
	            public void run() {
	            	httpServer.shutdownNow();
	            }
	        }));
	        
	        CompressionConfig compressionConfig = httpServer.getListener("grizzly").getCompressionConfig();
	        compressionConfig.setCompressionMode(compressionMode); // the mode
	        compressionConfig.setCompressionMinSize(compressionMinSize); // the min amount of bytes to compress
	        compressionConfig.setCompressableMimeTypes(compressableMimeTypes); // the mime types to compress
	        
	        addHttpHandler(httpServer, "/", "/");
	        
	        return httpServer;
		}
		
		private void addHttpHandler(HttpServer httpServer, String directory, String docRoot) {
			CLStaticHttpHandler httpHandler = new CLStaticHttpHandler(getClass().getClassLoader(), directory);
			httpHandler.setFileCacheEnabled(false); // Disable cache because it's very very slow
			httpServer.getServerConfiguration()
					  .addHttpHandler(httpHandler, docRoot);
		}
	}
}
