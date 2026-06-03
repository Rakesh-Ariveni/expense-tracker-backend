package com.rakesh.expensetracker.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@Service
public class ExpenseEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(ExpenseEventProducer.class);

    private static final String TOPIC =
            "expense-created-topic";

    private final KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate;

    public ExpenseEventProducer(
            KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishExpenseCreatedEvent(
            ExpenseCreatedEvent event
    ) {

        log.info(
                "Publishing expense created event for expenseId={}",
                event.getExpenseId()
        );

        kafkaTemplate.send(TOPIC, event);

        log.info(
                "Expense created event published successfully for expenseId={}",
                event.getExpenseId()
        );
    }
}