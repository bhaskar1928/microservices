package com.ecom.orderservice.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
// import lombok.Getter;
// import lombok.Setter;

@Entity
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "customer_email", nullable = false)
  private String customerEmail;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private String status; // e.g. NEW, CONFIRMED, REJECTED

  // private Instant createdAt = Instant.now();

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  // @Enumerated(EnumType.STRING)
  // private OrderStatus status = OrderStatus.PENDING;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItemEntity> items = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  // public Instant getCreatedAt() { return createdAt; }

  // public OrderStatus getStatus() { return status; }
  // public void setStatus(OrderStatus status) { this.status = status; }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<OrderItemEntity> getItems() {
    return items;
  }

  public void setItems(List<OrderItemEntity> items) {
    this.items.clear();
    if (items != null) {
      for (OrderItemEntity i : items)
        addItem(i);
    }
  }

  public void addItem(OrderItemEntity item) {
    item.setOrder(this);
    this.items.add(item);
  }
}
