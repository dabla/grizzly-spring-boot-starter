package be.dabla.boot.grizzly;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

import javax.inject.Inject;

import static java.util.Optional.ofNullable;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
public abstract class AbstractIntegrationTest {
    @Inject
    private ServletWebServerFactory webServerFactory;
    protected static WebServer webServer;

    @BeforeEach
    synchronized void setUp() {
        webServer = ofNullable(webServer).orElse(webServerFactory.getWebServer());
    }
}
