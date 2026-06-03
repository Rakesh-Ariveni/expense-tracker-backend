package com.rakesh.expensetracker.kafka.producer;

import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.rakesh.expensetracker.kafka.event.ExpenseCreatedEvent;

@ExtendWith(MockitoExtension.class)
class ExpenseEventProducerTest {

    @Mock
    private KafkaTemplate<String, ExpenseCreatedEvent> kafkaTemplate;

    @InjectMocks
    private ExpenseEventProducer producer;

    @Test
    void testPublishExpenseCreatedEvent() {

        ExpenseCreatedEvent event =
                new ExpenseCreatedEvent(
                        1L,
                        1L,
                        500.0,
                        "Food",
                        LocalDateTime.now()
                );

        producer.publishExpenseCreatedEvent(event);

        verify(kafkaTemplate)
                .send(
                        "expense-created-topic",
                        event
                );
    }
}