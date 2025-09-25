package com.ecom.inventory_services.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventory_services.events.InventoryUpdatedEvent;
import com.ecom.inventory_services.events.OrderPlacedEvent;
import com.ecom.inventory_services.model.ProductEntity;
import com.ecom.inventory_services.repo.ProductRepository;

@Component
public class OrderPlacedListener {
    private final ProductRepository productRepository;
    private final InventoryEventProducer producer;

  public OrderPlacedListener(ProductRepository productRepository, InventoryEventProducer producer) {
    this.productRepository = productRepository;
    this.producer = producer;
  }
@KafkaListener(topics = "${app.topic.orderPlaced}", groupId = "inventory-service",
                 containerFactory = "kafkaListenerContainerFactory")
  @Transactional
  public void onOrderPlaced(OrderPlacedEvent ev) {
for (var it : ev.getItems()) {
      ProductEntity p = productRepository.findById(it.getProductId()).orElse(null);
      if (p == null || p.getStockQty() < it.getQuantity()) {
        producer.publish(new InventoryUpdatedEvent(ev.getOrderId(), "REJECTED",
            "Insufficient stock for productId=" + it.getProductId()));
        return;
      }
    }
for (var it : ev.getItems()) {
      ProductEntity p = productRepository.findById(it.getProductId()).orElseThrow();
      p.setStockQty(p.getStockQty() - it.getQuantity());
      productRepository.save(p);
    }
        producer.publish(new InventoryUpdatedEvent(ev.getOrderId(), "RESERVED", null));
  }
}