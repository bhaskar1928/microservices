package com.ecom.inventory_services.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.inventory_services.model.ProcessedOrder;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long>{
 boolean existsByOrderId(Long orderId);
}