package com.ecom.orderservice.model;

import jakarta.persistence.*;

@Entity 
@Table(name="order_items")
public class OrderItemEntity {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long productId;
  private Integer quantity;
@ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private OrderEntity order;
public Long getId() { return id; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public Integer getQuantity() { return quantity; }
public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public OrderEntity getOrder() { return order; }
  public void setOrder(OrderEntity order) { this.order = order; }
}