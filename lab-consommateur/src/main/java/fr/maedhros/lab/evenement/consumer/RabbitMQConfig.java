package fr.maedhros.lab.evenement.consumer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue queue() {
        return new Queue("test", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("test");
    }

    @Bean
    Binding binding(final Queue queue, final DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory defaultListenerContainerFactory(final ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory rabbitListenerFactory = new SimpleRabbitListenerContainerFactory();
        rabbitListenerFactory.setConnectionFactory(connectionFactory);
        return rabbitListenerFactory;
    }
}