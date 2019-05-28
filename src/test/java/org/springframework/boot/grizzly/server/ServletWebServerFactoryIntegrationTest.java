package org.springframework.boot.grizzly.server;

import javax.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.grizzly.GrizzlyApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GrizzlyApplication.class)
public class ServletWebServerFactoryIntegrationTest {
    @Inject
    private ServletWebServerFactory webServerFactory;
    private static WebServer webServer;

    @BeforeEach
    synchronized void setUp() {
        webServer = ofNullable(webServer).orElse(webServerFactory.getWebServer());
    }

    @AfterAll
    static void tearDown() {
        webServer.stop();
    }

    @Test
    public void getPort() {
        assertThat(webServer.getPort()).isEqualTo(8080);
    }
}
