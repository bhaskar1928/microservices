package com.ecom.orderservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ecom.orderservice.events.OrderPlacedEvents;
@Component
public class OrderEventProducer {

       private final KafkaTemplate<String, OrderPlacedEvents> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, OrderPlacedEvents> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderPlacedEvents event) {
        kafkaTemplate.send("order-created", event);
        System.out.println(" Published event to Kafka: " + event);
    }
}