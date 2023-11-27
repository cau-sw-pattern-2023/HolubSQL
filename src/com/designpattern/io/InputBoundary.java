package com.designpattern.io;

import java.time.LocalDate;

public interface InputBoundary {
    void addNewProduct(String productName, int price);
    void removeProduct(String productName);
    void addStock(String productName, int count);
    void addStock(String productName, int count, LocalDate expirationDate);
    void sellStock(String productName);
    void sellStocks(String productName, int count);
    void requestProductInfo(String productName);
}
