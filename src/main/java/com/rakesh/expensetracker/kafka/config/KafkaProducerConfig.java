package com.rakesh.expensetracker.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }

    @Bean
    public ProducerFactory<String, ExpenseCreatedEvent> producerFactory(
            ObjectMapper objectMapper
    ) {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka:9092"
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        JsonSerializer<ExpenseCreatedEvent> serializer =
                new JsonSerializer<>(objectMapper);

        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                serializer
        );
    }

    @Bean
    public KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate(
            ProducerFactory<String, ExpenseCreatedEvent> producerFactory
    ) {

        return new KafkaTemplate<>(producerFactory);
    }
}