// package com.ecom.inventory_services.kafka;

// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;

// @Service
// public class OrderStatusProducer {
//      private final KafkaTemplate<String, String> kafkaTemplate;

//     public OrderStatusProducer(KafkaTemplate<String, String> kafkaTemplate) {
//         this.kafkaTemplate = kafkaTemplate;
//     }

//     public void sendStatusUpdate(String orderStatusJson) {
//         kafkaTemplate.send("order-status-updated", orderStatusJson);
//         System.out.println(" Sent order status update: " + orderStatusJson);
//     }
// }




package com.ecom.inventory_services.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderStatusProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatusUpdate(String orderStatusJson) {
        kafkaTemplate.send("order-status-updated", orderStatusJson);
        System.out.println("✅ Sent order status update: " + orderStatusJson);
    }
}
