package fr.maedhros.lab.evenement.producteur;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducteur {

    private final AtomicInteger compteur = new AtomicInteger(0);

    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedRate = 100L)
    public void produireMessage() {
        rabbitTemplate.convertAndSend("test", "", compteur.getAndIncrement());
    }
}
