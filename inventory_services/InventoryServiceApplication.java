package com.ecom.inventory_services;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class InventoryServiceApplication {

  @Value("${app.topic.orderPlaced}") private String orderPlacedTopic;
  @Value("${app.topic.inventoryUpdated}") private String inventoryUpdatedTopic;

public static void main(String[] args) {
    SpringApplication.run(InventoryServiceApplication.class, args);
  }

  @Bean
  public NewTopic orderPlaced() { return new NewTopic(orderPlacedTopic, 1, (short)1); }
@Bean
  public NewTopic inventoryUpdated() { return new NewTopic(inventoryUpdatedTopic, 1, (short)1); 
  }
}