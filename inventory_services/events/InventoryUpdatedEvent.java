package com.ecom.inventory_services.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdatedEvent {
    private Long orderId;
    private String status; 
    private String message;// "SUCCESS" or "FAILED"

    public InventoryUpdatedEvent(Long orderId, String status) {
        this.orderId = orderId;
        this.status = status;
}
}  
