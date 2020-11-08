package fr.maedhros.lab.evenement;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RabbitmqProducerApplication {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(RabbitmqProducerApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
