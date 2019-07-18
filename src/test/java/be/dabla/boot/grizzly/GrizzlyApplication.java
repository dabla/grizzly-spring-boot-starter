package be.dabla.boot.grizzly;

import javax.inject.Inject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class GrizzlyApplication {
    @Inject
    private ServletWebServerFactory webServer;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(GrizzlyApplication.class, args);
    }
}
