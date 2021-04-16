package be.dabla.boot.grizzly.hello;

import be.dabla.boot.grizzly.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloRestControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void sayHello() throws Exception {
        assertThat(new Scanner(new URL("http://localhost:" + webServer.getPort() + "/grizzly/index").openStream(), "UTF-8").useDelimiter("\\A").next()).isEqualTo("Hello world!");
    }
}
