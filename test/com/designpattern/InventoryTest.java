package com.designpattern;

import com.designpattern.exception.NoSuchProductException;
import com.designpattern.model.Product;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryTest {
    Inventory inventory = InventoryImpl.getInstance();
    final String testProductName = "test product name";

    private LocalDate getRandomDate(long minDay, long maxDay){
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return randomDate;
    }

    @Test
    @Order(100)
    void insertProduct() {
        // given
        inventory.addProduct(testProductName, 12345);

        // when
        Product product = inventory.findProduct(testProductName);

        // then
        assertThat(product.getName()).isEqualTo(testProductName);
        assertThat(product.getPrice()).isEqualTo(12345);
    }

    @Test
    @Order(900)
    void deleteProduct() {
        // given
        inventory.findProduct(testProductName);

        // when
        inventory.deleteProduct(testProductName);

        // then
        assertThatThrownBy(() -> inventory.findProduct(testProductName))
            .isInstanceOf(NoSuchProductException.class);

    }

    @Test
    @Order(110)
    void insertStock() {
        // given
        inventory.addStock(testProductName, 10);

        // when
        int productsQuantity = inventory.getProductsQuantity(testProductName);

        // then
        assertThat(productsQuantity).isEqualTo(10);
    }

    @Test
    @Order(120)
    void deleteStock() {
        // given
        int productsQuantity = inventory.getProductsQuantity(testProductName);
        assertThat(productsQuantity).isEqualTo(10);

        // when
        inventory.sell(testProductName, 1);

        // then
        productsQuantity = inventory.getProductsQuantity(testProductName);
        assertThat(productsQuantity).isEqualTo(9);
    }

    @Test
    @Order(140)
    void addDecayingStock(){
        LocalDate latterDate = getRandomDate(LocalDate.of(1999, 12, 31).toEpochDay(),
                LocalDate.of(2023, 11, 30).toEpochDay());
        LocalDate formerDate = getRandomDate(LocalDate.of(1999, 12, 31).toEpochDay(),
                LocalDate.of(2023, 11, 30).toEpochDay());


        if(latterDate.isBefore(formerDate)){
            LocalDate temp = latterDate;
            latterDate = formerDate;
            formerDate = temp;
        }

        inventory.addStock(testProductName, 1, latterDate);
        inventory.addStock(testProductName, 1, formerDate);


        inventory.sell(testProductName, 1);

        assertThat(inventory.getAllStocks().get(0).getExpirationDate().equals(latterDate))
                .isTrue();

    }
}