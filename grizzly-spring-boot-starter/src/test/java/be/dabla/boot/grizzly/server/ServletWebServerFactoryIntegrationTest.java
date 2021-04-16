package be.dabla.boot.grizzly.server;

import be.dabla.boot.grizzly.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
public class ServletWebServerFactoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getPort() {
        assertThat(webServer.getPort()).isEqualTo(8080);
    }
}
