// src/main/java/org/example/Product.java
package org.example.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    private String id;
    private String image;
    private String title;
    private String priceInteger;
    private String priceDecimal;
    private Category category;
    private String description;

    // 无参构造函数是 Gson 反序列化所必需的

    public Product(String id, String image, String title, String priceInteger, String priceDecimal, Category category, String description) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.priceInteger = priceInteger;
        this.priceDecimal = priceDecimal;
        this.category = category;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPriceInteger() { return priceInteger; }
    public void setPriceInteger(String priceInteger) { this.priceInteger = priceInteger; }
    public String getPriceDecimal() { return priceDecimal; }
    public void setPriceDecimal(String priceDecimal) { this.priceDecimal = priceDecimal; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", priceInteger='" + priceInteger + '\'' +
                ", priceDecimal='" + priceDecimal + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}