package com.ecom.inventory_services.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventory_services.model.ProcessedOrder;
import com.ecom.inventory_services.model.ProductEntity;
import com.ecom.inventory_services.repo.ProcessedOrderRepository;
import com.ecom.inventory_services.repo.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final ProcessedOrderRepository processedOrderRepo;

    public ProductService(ProductRepository productRepo, ProcessedOrderRepository processedOrderRepo) {
        this.productRepo = productRepo;
        this.processedOrderRepo = processedOrderRepo;
    }

    @Transactional
    public StatusResult deductStockForOrder(Long orderId, String sku, int qty) {
       
        if (processedOrderRepo.existsByOrderId(orderId)) {
            return new StatusResult(orderId, "ALREADY_PROCESSED", null);
        }

        Optional<ProductEntity> opt = productRepo.findBySku(sku);
        if (opt.isEmpty()) {
                        processedOrderRepo.save(new ProcessedOrder(orderId));
            return new StatusResult(orderId, "REJECTED", "Invalid SKU: " + sku);
        }

        ProductEntity product = opt.get();
        if (product.getStockQty() < qty) {
            processedOrderRepo.save(new ProcessedOrder(orderId));
            return new StatusResult(orderId, "REJECTED", "Insufficient stock for " + sku);
        }

       
        product.setStockQty(product.getStockQty() - qty);
        productRepo.save(product);
        processedOrderRepo.save(new ProcessedOrder(orderId));
        return new StatusResult(orderId, "CONFIRMED", null);
    }
    public static class StatusResult {
        public final Long orderId;
        public final String status;
        public final String reason;
        public StatusResult(Long orderId, String status, String reason) {
            this.orderId = orderId; this.status = status; this.reason = reason;
        }
    }
}