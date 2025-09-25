package com.ecom.inventory_services.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ecom.inventory_services.service.ProductService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderEventListener {

    private final ProductService productService;
    private final OrderStatusProducer statusProducer;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderEventListener(ProductService productService, OrderStatusProducer statusProducer) {
        this.productService = productService;
        this.statusProducer = statusProducer;
    }

    @KafkaListener(topics = "${app.topic.orderCreated}", groupId = "inventory-service-group")
    public void onOrderCreated(String message) {
        try {
            JsonNode root = mapper.readTree(message);
            Long orderId = root.has("orderId") ? root.get("orderId").asLong() : (root.has("id") ? root.get("id").asLong() : null);

    
            if (root.has("items") && root.get("items").isArray()) {
                
                JsonNode first = root.get("items").get(0);
                String sku = first.get("sku").asText();
                int qty = first.get("quantity").asInt();

                ProductService.StatusResult res = productService.deductStockForOrder(orderId, sku, qty);
                sendStatus(res);
            } else if (root.has("sku") && root.has("quantity")) {
                String sku = root.get("sku").asText();
                int qty = root.get("quantity").asInt();
                ProductService.StatusResult res = productService.deductStockForOrder(orderId, sku, qty);
                sendStatus(res);
            } else {
               
                String json = String.format("{\"orderId\":%s,\"status\":\"REJECTED\",\"reason\":\"malformed order payload\"}", orderId==null?"null":orderId.toString());
                statusProducer.sendStatusUpdate(json);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // send error status to orders so it doesn't hang
            try {
                String json = String.format("{\"orderId\":null,\"status\":\"ERROR\",\"reason\":\"%s\"}", ex.getMessage());
                statusProducer.sendStatusUpdate(json);
            } catch (Exception ignore) {}
        }
    }

    private void sendStatus(ProductService.StatusResult res) {
        String out;
        if (res.reason == null) {
            out = String.format("{\"orderId\":%d,\"status\":\"%s\"}", res.orderId, res.status);
        } else {
            out = String.format("{\"orderId\":%d,\"status\":\"%s\",\"reason\":\"%s\"}", res.orderId, res.status, res.reason);
        }
        statusProducer.sendStatusUpdate(out);
    }
}