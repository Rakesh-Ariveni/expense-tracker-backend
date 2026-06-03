package com.rakesh.expensetracker.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic expenseCreatedTopic() {

        return new NewTopic(
                "expense-created-topic",
                1,
                (short) 1
        );
    }

    @Bean
    public NewTopic expenseCreatedDLTTopic() {

        return new NewTopic(
                "expense-created-topic-dlt",
                1,
                (short) 1
        );
    }
}