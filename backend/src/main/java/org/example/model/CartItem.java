package org.example.model;

import com.google.gson.annotations.SerializedName;

public class CartItem extends Product{
    private int quantity;
    private boolean selected;

    public CartItem() {};
    public CartItem(Long id, String image, String title, String priceInteger, String priceDecimal, Category category, String description, int quantity, boolean selected) {
        super(id,image,title,priceInteger,priceDecimal,category,description);
        this.quantity = quantity;
        this.selected = selected;
    }

    // Getters and Setters

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", quantity=" + quantity +
                ", selected=" + selected +
                '}';
    }
}