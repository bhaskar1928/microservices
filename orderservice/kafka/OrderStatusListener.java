package com.ecom.orderservice.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecom.orderservice.repo.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderStatusListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "${app.topic.orderStatusUpdated:order-status-updated}", groupId = "order-service-group")
    public void onStatusUpdate(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            if (!node.has("orderId")) {
                System.out.println("order-status-updated missing orderId: " + message);
                return;
            }
            Long orderId = node.get("orderId").isNull() ? null : node.get("orderId").asLong();
            String status = node.has("status") ? node.get("status").asText() : null;
            String reason = node.has("reason") ? node.get("reason").asText() : null;

            if (orderId == null) return;

            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus(status);
                orderRepository.save(order);
                System.out.println("Order " + orderId + " set to " + status + (reason != null ? " reason:"+reason : ""));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}