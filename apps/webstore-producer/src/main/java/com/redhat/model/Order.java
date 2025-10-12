package com.redhat.model;

import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private List<String> items;

    public Order() {

    }

    public Order(String orderId, String customerId, List<String> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
