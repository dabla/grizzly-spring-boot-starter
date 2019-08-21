package be.dabla.boot.grizzly.server;

import be.dabla.boot.grizzly.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServletWebServerFactoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getPort() {
        assertThat(webServer.getPort()).isEqualTo(8080);
    }
}
