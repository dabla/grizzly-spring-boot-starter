package be.dabla.boot.grizzly.hello;

import be.dabla.boot.grizzly.AbstractIntegrationTest;
import java.net.URL;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResourceIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void sayHello() throws Exception {
        assertThat(new Scanner(new URL("http://localhost:8080/grizzly/hello/world").openStream(), "UTF-8").useDelimiter("\\A").next()).isEqualTo("Hello world!");
    }
}
