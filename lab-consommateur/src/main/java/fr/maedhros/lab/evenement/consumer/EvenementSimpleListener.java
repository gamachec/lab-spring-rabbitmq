package fr.maedhros.lab.evenement.consumer;

import java.util.Random;

import fr.maedhros.lab.evenement.annotation.EvenementHandler;
import fr.maedhros.lab.evenement.annotation.EvenementListener;
import fr.maedhros.lab.evenement.metier.EvenementAccuseReception;
import fr.maedhros.lab.evenement.metier.EvenementSimple;
import fr.maedhros.lab.evenement.service.EvenementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EvenementListener
@RequiredArgsConstructor
public class EvenementSimpleListener {

    private final EvenementService evenementService;

    @Value("${spring.application.name}")
    private String appName;

    @SuppressWarnings("unused")
    @EvenementHandler(threads = 3)
    public void traiterEvenementSimple(final EvenementSimple evenementSimple) {
        if (new Random().nextInt(10) <= 1) {
            log.error("Erreur pour le message {}, rejeu programmé", evenementSimple.getMessage());
            evenementService.planifierRejeuEvenement(evenementSimple);
            return;
        }
        log.info("Message traité : {}", evenementSimple.getMessage());

        final var accuseReception = EvenementAccuseReception.builder()//
            .message(evenementSimple.getMessage())//
            .app(appName)//
            .build();
        evenementService.produireEvenement(accuseReception);
    }
}
