package com.famesmart.privilege.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    static final public String topicExchangeName = "saas-ws-push-exchange";

    static final public String queueName = "saas-ws-push-queue";

    static final public String deadLetterExchangeName = "saas-ws-push-dlx";

    static final public String deadLetterQueueName = "saas-ws-push-dlq";

    static final public String deadLetterRoutingKey = "saas-ws-push-fail";

    @Bean
    Queue queue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", deadLetterExchangeName);
        arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);
        return new Queue(queueName, true, false, false, arguments);
    }

    @Bean
    Queue dlq() {
        return QueueBuilder.durable(deadLetterQueueName).build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("#");
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with(deadLetterRoutingKey);
    }
}
