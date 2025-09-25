package com.ecom.inventory_services.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventory_services.kafka.OrderStatusProducer;
import com.ecom.inventory_services.model.ProductEntity;
import com.ecom.inventory_services.repo.ProductRepository;

import java.util.List;



@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo;
    @Autowired
    private OrderStatusProducer statusProducer;

    public ProductController(ProductRepository repo) { this.repo = repo; }
@PostMapping("/seed")
    public List<ProductEntity> seed() {
        if (repo.count() == 0) {
            ProductEntity p1 = new ProductEntity();
            p1.setSku("SKU-APPLE"); p1.setName("Apple iPhone Case"); p1.setStockQty(10);

            ProductEntity p2 = new ProductEntity();
            p2.setSku("SKU-CABLE"); p2.setName("USB-C Cable"); p2.setStockQty(25);

            repo.save(p1); repo.save(p2);
        }
        return repo.findAll();
    }
@PostMapping
    public ProductEntity create(@RequestBody ProductEntity body) {
        return repo.save(body);
    }
@GetMapping
    public List<ProductEntity> list() {
        return repo.findAll();
    }
@GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
@PutMapping("{id}/stock")
    public ResponseEntity<?> setStock(@PathVariable Long id, @RequestParam Integer qty) {
        return repo.findById(id).map(p -> {
            p.setStockQty(qty);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
@PatchMapping("{id}/stock")
    public ResponseEntity<?> changeStock(@PathVariable Long id, @RequestParam Integer delta) {
        return repo.findById(id).map(p -> {
            p.setStockQty(p.getStockQty() + delta);
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
@DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/inventory/test")
    public String testInventoryService() {
        return "Inventory Service is running!";
    }
    @PostMapping("/deduct")
public String deductStock(@RequestParam String sku, @RequestParam int qty, @RequestParam Long orderId) {
    ProductEntity p = repo.findBySku(sku).orElseThrow();
    if (p.getStockQty() >= qty) {
        p.setStockQty(p.getStockQty() - qty);
        repo.save(p);

        String json = String.format("{\"orderId\":%d,\"status\":\"CONFIRMED\"}", orderId);
        statusProducer.sendStatusUpdate(json);

        return "Stock deducted, order confirmed.";
    } else {
    
        String json = String.format("{\"orderId\":%d,\"status\":\"REJECTED\"}", orderId);
        statusProducer.sendStatusUpdate(json);

        return "Not enough stock, order rejected.";
    }

}
}