package org.example.model;

public class CartItem {
    private String id;
    private String image;
    private String title;
    private int priceInteger;
    private int priceDecimal;
    private int quantity;
    private boolean selected;

    public CartItem(String id, String image, String title, int priceInteger, int priceDecimal, int quantity, boolean selected) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.priceInteger = priceInteger;
        this.priceDecimal = priceDecimal;
        this.quantity = quantity;
        this.selected = selected;

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

    public boolean isValid() {
        return (id!= null && !id.isEmpty() && quantity >0);
    }
}