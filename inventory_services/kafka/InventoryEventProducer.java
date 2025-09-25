
package com.ecom.inventory_services.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecom.inventory_services.events.InventoryUpdatedEvent;

@Service
public class InventoryEventProducer {

    private static final String TOPIC = "inventory-updated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

  
    public void publish(InventoryUpdatedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println(" Published InventoryUpdatedEvent: " + event);
    }
}