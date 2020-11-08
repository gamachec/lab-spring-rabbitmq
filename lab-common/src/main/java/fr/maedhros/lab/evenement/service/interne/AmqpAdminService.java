package fr.maedhros.lab.evenement.service.interne;

import fr.maedhros.lab.evenement.annotation.EvenementAvecRejeuPlanifie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmqpAdminService {

    public static final String REJEU_ROUTING_KEY = "rejeu";
    public static final String PRIMARY_ROUTING_KEY = "";

    private final AmqpAdmin amqpAdmin;

    @Value("${spring.application.name}")
    private String nomGroupeConsommateur;

    public void initialiserExchange(final String nomEvenement) {
        log.info("Initialisation de l'exchange {}", nomEvenement);
        final var exchange = new DirectExchange(nomEvenement, true, false);
        amqpAdmin.declareExchange(exchange);
    }

    public Queue initialiserQueue(final Class<?> evenement) {
        final var nomEvenement = evenement.getSimpleName();
        final Queue queuePrincipale = initialiserQueuePrincipale(nomEvenement);

        if (evenement.isAnnotationPresent(EvenementAvecRejeuPlanifie.class)) {
            final int delaisSecondes = evenement.getAnnotation(EvenementAvecRejeuPlanifie.class).delaisSecondes();
            initialiserQueueRejeu(nomEvenement, delaisSecondes);
        }

        return queuePrincipale;
    }

    private Queue initialiserQueuePrincipale(final String nomEvenement) {
        final var nomQueuePrincipale = nomQueuePrincipale(nomEvenement);

        log.info("Déclaration de la queue {}", nomQueuePrincipale);
        final var queuePrincipale = QueueBuilder.durable(nomQueuePrincipale).build();
        final var bindingPrincipal = new Binding(nomQueuePrincipale, Binding.DestinationType.QUEUE, nomEvenement, PRIMARY_ROUTING_KEY, null);

        amqpAdmin.declareQueue(queuePrincipale);
        amqpAdmin.declareBinding(bindingPrincipal);

        return queuePrincipale;
    }

    private void initialiserQueueRejeu(final String nomEvenement, final int delaisSecondes) {
        final var nomQueuePrincipale = nomQueuePrincipale(nomEvenement);
        final var nomQueueRejeu = nomQueueRejeu(nomEvenement);

        log.info("Déclaration de la queue de rejeu planifié {} avec un délais de {} secondes", nomQueueRejeu, delaisSecondes);
        final var queueRetry = QueueBuilder.durable(nomQueueRejeu)//
            .deadLetterExchange(nomEvenement)//
            .deadLetterRoutingKey(nomGroupeConsommateur)//
            .ttl(delaisSecondes * 1000)//
            .build();

        final var bindingRejeu = new Binding(nomQueueRejeu, Binding.DestinationType.QUEUE, nomEvenement, cleRoutageRejeu(), null);
        final var bindingReprise = new Binding(nomQueuePrincipale, Binding.DestinationType.QUEUE, nomEvenement, nomGroupeConsommateur, null);

        amqpAdmin.declareQueue(queueRetry);
        amqpAdmin.declareBinding(bindingRejeu);
        amqpAdmin.declareBinding(bindingReprise);
    }

    public String cleRoutageRejeu() {
        return String.format("%s.%s", nomGroupeConsommateur, REJEU_ROUTING_KEY);
    }

    public String nomQueueRejeu(final String nomEvenement) {
        return String.format("%s.%s.%s", nomGroupeConsommateur, nomEvenement, REJEU_ROUTING_KEY);
    }

    public String nomQueuePrincipale(final String nomEvenement) {
        return String.format("%s.%s", nomGroupeConsommateur, nomEvenement);
    }
}
