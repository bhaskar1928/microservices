package com.ecom.inventory_services.exception;

public class OutOfStockException extends RuntimeException {
     
     public OutOfStockException(String message) { 
          super(message); 
     }
}