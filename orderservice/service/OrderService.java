
package com.ecom.orderservice.service;

import com.ecom.orderservice.dto.PlaceOrderRequest;
import com.ecom.orderservice.events.OrderPlacedEvents;
import com.ecom.orderservice.kafka.OrderEventProducer;
import com.ecom.orderservice.model.OrderEntity;
import com.ecom.orderservice.repo.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer producer;

    public OrderService(OrderRepository orderRepository, OrderEventProducer producer) {
        this.orderRepository = orderRepository;
        this.producer = producer;
    }
    
    @Transactional
    public OrderEntity placeOrder(PlaceOrderRequest request) {
        
        OrderEntity order = new OrderEntity();
        order.setSku(request.getSku());
        order.setQuantity(request.getQuantity());
        order.setStatus("PENDING");

        OrderEntity saved = orderRepository.save(order);
        OrderPlacedEvents.Item item = new OrderPlacedEvents.Item(
                saved.getSku(),
                saved.getQuantity()
        );

        OrderPlacedEvents event = new OrderPlacedEvents(
                saved.getId(),
                "customer@example.com",
                Collections.singletonList(item)
        );

        producer.publish(event);

        return saved;
    }
}