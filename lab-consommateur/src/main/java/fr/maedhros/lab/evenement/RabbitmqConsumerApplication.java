package fr.maedhros.lab.evenement;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class RabbitmqConsumerApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(RabbitmqConsumerApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
