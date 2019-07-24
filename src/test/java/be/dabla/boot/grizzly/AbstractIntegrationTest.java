package be.dabla.boot.grizzly;

import javax.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Optional.ofNullable;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GrizzlyApplication.class)
public abstract class AbstractIntegrationTest {
    @Inject
    private ServletWebServerFactory webServerFactory;
    protected static WebServer webServer;

    @BeforeEach
    synchronized void setUp() {
        webServer = ofNullable(webServer).orElse(webServerFactory.getWebServer());
        webServer.start();
    }

    @AfterAll
    static void tearDown() {
        webServer.stop();
        webServer = null;
    }
}
