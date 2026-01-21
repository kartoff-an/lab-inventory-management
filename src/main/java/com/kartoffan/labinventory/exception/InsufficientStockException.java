package com.kartoffan.labinventory.exception;

public class InsufficientStockException extends RuntimeException{
  
  public InsufficientStockException(String itemName) {
    super("Insufficient stock for item: " + itemName);
  }

  public InsufficientStockException(String itemName, double available, double requested) {
    super("Insufficient stock for item: " + itemName + ". Available: " + available + ", Requested: " + requested);
  }
}
