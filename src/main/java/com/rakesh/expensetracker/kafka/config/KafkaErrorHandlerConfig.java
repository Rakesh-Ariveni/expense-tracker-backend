package com.rakesh.expensetracker.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate
    ) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (ConsumerRecord<?, ?> record, Exception ex) ->
                                new org.apache.kafka.common.TopicPartition(
                                        record.topic() + "-dlt",
                                        record.partition()
                                )
                );

        FixedBackOff fixedBackOff =
                new FixedBackOff(
                        2000L,
                        3
                );

        return new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );
    }
}