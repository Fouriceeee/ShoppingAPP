// src/main/java/org/example/Product.java
package org.example.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    private String id;
    private String image;
    private String title;
    private int priceInteger;
    private int priceDecimal;
    private Category category;
    private String description;

    // 无参构造函数是 Gson 反序列化所必需的

    public Product(String id, String image, String title, int priceInteger, int priceDecimal, Category category, String description) {
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
    public int getPriceInteger() { return priceInteger; }
    public void setPriceInteger(int priceInteger) { this.priceInteger = priceInteger; }
    public int getPriceDecimal() { return priceDecimal; }
    public void setPriceDecimal(int priceDecimal) { this.priceDecimal = priceDecimal; }
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

    public Boolean isValid() {
        return (id != null && !id.isEmpty() && priceInteger >= 0 && priceDecimal >= 0 && priceDecimal <= 99);
    }

    //返回this的拷贝
    public Product copy() {
        return new Product(id,image,title,priceInteger,priceDecimal,category,description);
    }

    //从newProduct拷贝除id的内容到this
    public void copyFrom(Product newProduct) {
        image = newProduct.getImage();
        title = newProduct.getTitle();
        priceInteger = newProduct.getPriceInteger();
        priceDecimal = newProduct.getPriceDecimal();
        category = newProduct.getCategory();
        description = newProduct.getDescription();
    }

    //创建新CartItem
    public CartItem toCartItem(int quantity,boolean selected) {
        return new CartItem(id,image,title,priceInteger,priceDecimal,quantity,selected);
    }
}