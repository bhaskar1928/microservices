package com.ecom.orderservice.kafka;

import com.ecom.orderservice.repo.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryUpdateListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public InventoryUpdateListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "${app.topic.inventoryUpdated}", groupId = "order-service-group")
    public void handleInventoryUpdated(String message) {
        try {
            JsonNode node = mapper.readTree(message);
            if (!node.has("orderId")) {
                System.out.println("inventory-updated missing orderId: " + message);
                return;
            }
    Long orderId = node.get("orderId").isNull() ? null : node.get("orderId").asLong();
            String status = node.has("status") ? node.get("status").asText() : null;
            String reason = node.has("reason") ? node.get("reason").asText() : null;

            if (orderId == null) {
                System.out.println("inventory-updated has null orderId");
                return;
            }

            orderRepository.findById(orderId).ifPresent(order -> {
                if ("RESERVED".equalsIgnoreCase(status)) {
                    order.setStatus("CONFIRMED");
                } else if ("REJECTED".equalsIgnoreCase(status)) {
                    order.setStatus("REJECTED");
                }
                orderRepository.save(order);
                System.out.println("📥 Order " + orderId + " status updated to " + order.getStatus() + (reason != null ? (" reason:" + reason) : ""));
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}