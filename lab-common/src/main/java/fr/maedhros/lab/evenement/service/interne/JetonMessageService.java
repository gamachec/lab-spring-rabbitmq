package fr.maedhros.lab.evenement.service.interne;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Component;

@SuppressWarnings("MethodMayBeStatic")
@Slf4j
@Component
public class JetonMessageService {

    public void positionnerJetonDansMessage(final MessageProperties messageProperties) {
        // Récupérer dans le thread local les informations du jeton
        messageProperties.setHeader("utilisateur", "TST1");
        messageProperties.setHeader("idSession", 1L);
    }

    public void recupererJetonDepuisMessage(final MessageProperties messageProperties) {
        final var utilisateur = messageProperties.getHeader("utilisateur");
        final var idSession = messageProperties.getHeader("idSession");
        // Stocker dans le thread local les informations du jeton
    }
}
