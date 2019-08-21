package be.dabla.boot.grizzly.server;

import java.io.IOException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrizzlyWebServerTest {
    private static final int PORT = 80;

    @Mock
    private HttpServer httpServer;
    @Mock
    private NetworkListener networkListener;

    @Test
    void start() throws IOException {
        new GrizzlyWebServer(httpServer).start();

        InOrder inOrder = inOrder(httpServer, networkListener);
        inOrder.verify(httpServer).start();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void stop() throws IOException {
        new GrizzlyWebServer(httpServer).stop();

        InOrder inOrder = inOrder(httpServer, networkListener);
        inOrder.verify(httpServer).stop();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void getPort() throws IOException {
        when(httpServer.getListener("grizzly")).thenReturn(networkListener);
        when(networkListener.getPort()).thenReturn(PORT);

        assertThat(new GrizzlyWebServer(httpServer).getPort()).isEqualTo(PORT);

        InOrder inOrder = inOrder(httpServer, networkListener);
        inOrder.verify(httpServer).getListener("grizzly");
        inOrder.verify(networkListener).getPort();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void close() throws IOException {
        new GrizzlyWebServer(httpServer).close();

        InOrder inOrder = inOrder(httpServer, networkListener);
        inOrder.verify(httpServer).stop();
        inOrder.verifyNoMoreInteractions();
    }
}