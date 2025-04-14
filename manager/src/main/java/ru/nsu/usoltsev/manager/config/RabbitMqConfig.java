package ru.nsu.usoltsev.manager.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String MANAGER_TO_WORKER_QUEUE = "manager.to.worker.queue";
    public static final String MANAGER_TO_WORKER_EXCHANGE = "manager.to.worker.exchange";
    public static final String MANAGER_TO_WORKER_ROUTING_KEY = "manager.to.worker.key";

    public static final String WORKER_TO_MANAGER_QUEUE = "worker.to.manager.queue";
    public static final String WORKER_TO_MANAGER_EXCHANGE = "worker.to.manager.exchange";
    public static final String WORKER_TO_MANAGER_ROUTING_KEY = "worker.to.manager.key";

    @Bean
    public Queue managerToWorkerQueue() {
        return QueueBuilder.durable(MANAGER_TO_WORKER_QUEUE).build();
    }

    @Bean
    public DirectExchange managerToWorkerExchange() {
        return new DirectExchange(MANAGER_TO_WORKER_EXCHANGE);
    }

    @Bean
    public Binding managerToWorkerBinding() {
        return BindingBuilder
                .bind(managerToWorkerQueue())
                .to(managerToWorkerExchange())
                .with(MANAGER_TO_WORKER_ROUTING_KEY);
    }

    @Bean
    public Queue workerToManagerQueue() {
        return QueueBuilder.durable(WORKER_TO_MANAGER_QUEUE).build();
    }

    @Bean
    public DirectExchange workerToManagerExchange() {
        return new DirectExchange(WORKER_TO_MANAGER_EXCHANGE);
    }

    @Bean
    public Binding workerToManagerBinding() {
        return BindingBuilder
                .bind(workerToManagerQueue())
                .to(workerToManagerExchange())
                .with(WORKER_TO_MANAGER_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());

        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(12)
                .backOffOptions(1000, 2.0, 10000)
                .build());

        return factory;
    }
}
