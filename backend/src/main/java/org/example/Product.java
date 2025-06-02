package org.example;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("NAME")
    private String name;

    @SerializedName("CATEGORY")
    private String category;

    @SerializedName("INVENTORY")
    private int inventory;

    @SerializedName("PRICE")
    private long price;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getInventory() {
        return inventory;
    }

    public long getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}

