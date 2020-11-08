package fr.maedhros.lab.evenement.producteur;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import fr.maedhros.lab.evenement.annotation.EvenementHandler;
import fr.maedhros.lab.evenement.annotation.EvenementListener;
import fr.maedhros.lab.evenement.metier.EvenementAccuseReception;
import fr.maedhros.lab.evenement.metier.EvenementSimple;
import fr.maedhros.lab.evenement.service.EvenementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EvenementListener
@RequiredArgsConstructor
public class MessageProducteur {

    private final EvenementService evenementService;

    private final AtomicInteger compteur = new AtomicInteger(0);

    @Scheduled(fixedRate = 1000L)
    public void produireMessage() {
        final var evenementSimple = EvenementSimple.builder()//
            .id(UUID.randomUUID().toString())//
            .message(String.valueOf(compteur.getAndIncrement()))//
            .build();

        evenementService.produireEvenement(evenementSimple);
    }

    @SuppressWarnings("unused")
    @EvenementHandler
    public static void traiterAccuseReception(final EvenementAccuseReception accuseReception) {
        log.info("Ack {} : {}", accuseReception.getApp(), accuseReception.getMessage());
    }
}
