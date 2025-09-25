package com.ecom.orderservice.events;

public class InventoryUpdatedEvent {
  private Long orderId;
  private String status; // RESERVED or REJECTED
  private String reason;
public Long getOrderId() { return orderId; }
  public void setOrderId(Long orderId) { this.orderId = orderId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
}