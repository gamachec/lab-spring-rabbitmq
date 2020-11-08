package fr.maedhros.lab.evenement.service;

import static fr.maedhros.lab.evenement.service.interne.AmqpAdminService.PRIMARY_ROUTING_KEY;
import static java.util.Objects.requireNonNull;

import fr.maedhros.lab.evenement.annotation.EvenementAvecRejeuPlanifie;
import fr.maedhros.lab.evenement.metier.Evenement;
import fr.maedhros.lab.evenement.service.interne.AmqpAdminService;
import fr.maedhros.lab.evenement.service.interne.JetonMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final AmqpTemplate amqpTemplate;

    private final AmqpAdminService amqpAdminService;

    private final JetonMessageService jetonMessageService;

    /**
     * Produit un événement
     *
     * @param evenement
     */
    public void produireEvenement(final Evenement evenement) {
        requireNonNull(evenement);
        produireEvenement(evenement, PRIMARY_ROUTING_KEY);
    }

    /**
     * Planifie le rejeu d'un évenement
     *
     * @param evenement
     */
    public void planifierRejeuEvenement(final Evenement evenement) {
        requireNonNull(evenement);
        if (!evenement.getClass().isAnnotationPresent(EvenementAvecRejeuPlanifie.class)) {
            throw new IllegalArgumentException("Impossible de planifier un rejeu sur un évenement qui ne possède pas l'annotation " +
                EvenementAvecRejeuPlanifie.class.getSimpleName());
        }
        produireEvenement(evenement, amqpAdminService.cleRoutageRejeu());
    }

    private void produireEvenement(final Evenement evenement, final String routingKey) {
        final var nomEvenement = evenement.getClass().getSimpleName();
        amqpTemplate.convertAndSend(nomEvenement, routingKey, evenement, message -> {
            final var messageProperties = message.getMessageProperties();
            jetonMessageService.positionnerJetonDansMessage(messageProperties);
            return message;
        });
    }
}
