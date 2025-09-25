package com.ecom.inventory_services.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventory_services.events.InventoryUpdatedEvent;
import com.ecom.inventory_services.kafka.InventoryEventProducer;
import com.ecom.inventory_services.model.ProductEntity;
import com.ecom.inventory_services.repo.ProductRepository;



@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryEventProducer producer;

    public InventoryService(ProductRepository productRepository, InventoryEventProducer producer) {
        this.productRepository = productRepository;
        this.producer = producer;
    }

    @Transactional
    public void deductStock(Long orderId, String sku, int quantity) {
        ProductEntity product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found: " + sku));

        if (product.getStockQty() >= quantity) {
            product.setStockQty(product.getStockQty() - quantity);
            productRepository.save(product);

            // publish success
            InventoryUpdatedEvent event = new InventoryUpdatedEvent(orderId, "SUCCESS");
            producer.publish(event);
        } else {
            // publish failed
            InventoryUpdatedEvent event = new InventoryUpdatedEvent(orderId, "FAILED");
            producer.publish(event);
        }
    }
}