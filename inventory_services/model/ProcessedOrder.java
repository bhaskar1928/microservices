package com.ecom.inventory_services.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_orders")
public class ProcessedOrder {
    
     @Id
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt = LocalDateTime.now();

    public ProcessedOrder() {}
    public ProcessedOrder(Long orderId) { this.orderId = orderId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
    

