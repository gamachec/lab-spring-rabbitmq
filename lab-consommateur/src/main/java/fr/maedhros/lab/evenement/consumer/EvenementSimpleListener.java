package fr.maedhros.lab.evenement.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvenementSimpleListener {

    private final ConfigurableApplicationContext applicationContext;

    @Scheduled(initialDelay = 5000L, fixedDelay = 5000L)
    public void tuStop() {
        log.info("ARRET DE L'APPLICATION ----------------------------------");
        applicationContext.close();
    }

    @SuppressWarnings("unused")
    @RabbitListener(queues = "test")
    public static void traiterEvenementSimple(final int comteur) {
        log.info("Message traité : {}", comteur);
    }
}
