package com.rakesh.expensetracker.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, ExpenseCreatedEvent> consumerFactory() {

        JsonDeserializer<ExpenseCreatedEvent> deserializer =
                new JsonDeserializer<>(ExpenseCreatedEvent.class);

        deserializer.addTrustedPackages("*");

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092"
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "expense-group"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ExpenseCreatedEvent>
    kafkaListenerContainerFactory(
            DefaultErrorHandler errorHandler
    ) {

        ConcurrentKafkaListenerContainerFactory<String, ExpenseCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}