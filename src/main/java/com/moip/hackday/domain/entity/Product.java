package com.moip.hackday.domain.entity;

import org.springframework.data.annotation.Id;

public class Product {

    @Id
    private String id;

    private String name;

    private String price;

    private String url;

    private String sellerName;

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public Product setPrice(String price) {
        this.price = price;
        return this;
    }

    public Product setUrl(String url) {
        this.url = url;
        return this;
    }

    public Product setSellerName(String sellerName) {
        this.sellerName = sellerName;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public String getSellerName() {
        return sellerName;
    }
}
