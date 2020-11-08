package fr.maedhros.lab.evenement.config;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import fr.maedhros.lab.evenement.annotation.EvenementHandler;
import fr.maedhros.lab.evenement.annotation.EvenementListener;
import fr.maedhros.lab.evenement.metier.Evenement;
import fr.maedhros.lab.evenement.service.interne.AmqpAdminService;
import fr.maedhros.lab.evenement.service.interne.JetonMessageService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.reflections.Reflections;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Préparation des exchanges, binding, queues, et démarrange des listeners pour chacun des évenements.
 */
@Configuration
@RequiredArgsConstructor
public class CommonEvenementConfiguration implements InitializingBean, ApplicationContextAware {

    private final ConnectionFactory connectionFactory;

    private final MessageConverter messageConverter;

    private final AmqpAdminService amqpAdminService;

    private final JetonMessageService jetonMessageService;

    @Value("${spring.rabbitmq.event-detection.base-package}")
    private String basePrefixeScanEvenements;

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        initialiserExchanges();
        initialiserQueuesEtListeners();
    }

    /**
     * Recherche toutes les implémentations visibles de {@link Evenement} pour initialiser les exchanges
     */
    private void initialiserExchanges() {
        final var reflections = new Reflections(basePrefixeScanEvenements);
        final var evenements = reflections.getSubTypesOf(Evenement.class);

        evenements.stream()//
            .map(Class::getSimpleName)//
            .forEach(amqpAdminService::initialiserExchange);
    }

    /**
     * Recherche toutes les classes annotées {@link EvenementListener}, puis enregistre les méthodes annotées {@link EvenementHandler} sur les queues
     * en fonction du type de leur paramètre.
     */
    private void initialiserQueuesEtListeners() {
        final var beansEvenementListeners = applicationContext.getBeansWithAnnotation(EvenementListener.class);

        beansEvenementListeners.forEach((nom, bean) -> //
            Arrays.stream(bean.getClass().getDeclaredMethods())//
                .filter(method -> method.isAnnotationPresent(EvenementHandler.class))//
                .forEach(this::configurerEvenementHandler)//
        );
    }

    private void configurerEvenementHandler(final Method methodEvenementHandler) {
        verifierNombreArguments(methodEvenementHandler);
        verifierTypeRetour(methodEvenementHandler.getReturnType());

        final Parameter parametre = methodEvenementHandler.getParameters()[0];
        verifierTypeParametre(parametre);
        final Class<?> typeEvenement = parametre.getType();
        final Queue queue = amqpAdminService.initialiserQueue(typeEvenement);

        demarrerHandlerPourQueue(methodEvenementHandler, queue);
    }

    private void demarrerHandlerPourQueue(final Method method, final Queue queue) {
        final Object listenerBean = applicationContext.getBean(method.getDeclaringClass());

        final int threads = method.getAnnotation(EvenementHandler.class).threads();

        final MessageListenerAdapter messageListener = new MessageListenerAdapter(listenerBean, messageConverter);
        messageListener.setDefaultListenerMethod(method.getName());

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queue);
        container.setConcurrentConsumers(threads);
        container.setMessageListener(messageListener);
        container.setAfterReceivePostProcessors(message -> {
            final MessageProperties messageProperties = message.getMessageProperties();
            jetonMessageService.recupererJetonDepuisMessage(messageProperties);
            return message;
        });

        container.start();
    }

    private static void verifierNombreArguments(final Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException("Un " + EvenementHandler.class.getSimpleName() + " ne doit posséder qu'un seul argument");
        }
    }

    private static void verifierTypeRetour(final Class<?> typeRetour) {
        if (!typeRetour.equals(Void.TYPE)) {
            throw new IllegalArgumentException("Un " + EvenementHandler.class.getSimpleName() + " ne doit pas avoir de retour");
        }
    }

    private static void verifierTypeParametre(final Parameter parametre) {
        if (!Evenement.class.isAssignableFrom(parametre.getType())) {
            throw new IllegalArgumentException(
                "Un " + EvenementHandler.class.getSimpleName() + " doit posséder un argument implémentant " + Evenement.class.getSimpleName());
        }
    }
}
