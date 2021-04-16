package be.dabla.boot.grizzly.http;

import org.apache.http.client.utils.URIBuilder;
import org.glassfish.grizzly.http.CompressionConfig;
import org.glassfish.grizzly.http.CompressionConfig.CompressionMode;
import org.glassfish.grizzly.http.server.HttpServer;
import org.springframework.util.unit.DataSize;

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
	
	public BuildableHttpServer withPath(String path) throws URISyntaxException {
		return new BuildableHttpServer(this.uriBuilder.setPath(path).build());
	}
	
	public static class BuildableHttpServer {
		private final URI uri;
		private CompressionMode compressionMode = OFF;
		private Long compressionMinSize = DataSize.ofKilobytes(2).toBytes();
		private Set<String> compressableMimeTypes = emptySet();

		private BuildableHttpServer(URI uri) {
			this.uri = uri;
		}
		
		public BuildableHttpServer withCompressionMode(CompressionMode compressionMode) {
			this.compressionMode = compressionMode;
			return this;
		}

		public BuildableHttpServer withCompressionMinSize(Long compressionMinSize) {
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
	        final HttpServer httpServer = createHttpServer(uri, false);

	        getRuntime().addShutdownHook(new Thread(new Runnable() {
	            @Override
	            public void run() {
	            	httpServer.shutdownNow();
	            }
	        }));
	        
	        CompressionConfig compressionConfig = httpServer.getListener("grizzly").getCompressionConfig();
	        compressionConfig.setCompressionMode(compressionMode); // the mode
	        compressionConfig.setCompressionMinSize(compressionMinSize.intValue()); // the min amount of bytes to compress
	        compressionConfig.setCompressableMimeTypes(compressableMimeTypes); // the mime types to compress
	        
	        return httpServer;
		}
	}
}
