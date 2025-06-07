package org.example.model;

import com.google.gson.annotations.SerializedName;
import org.example.Group;

import java.util.ArrayList;

public class User {
    @SerializedName("ID")
    private String id;

    @SerializedName("NAME")
    private String name;

    @SerializedName("E-MAIL")
    private String email;

    @SerializedName("PASSWORD")
    private String password;

    @SerializedName("GROUP")
    private Group group = Group.CUSTOMER;

    @SerializedName("CART")
    private ArrayList<String> cart = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Group getGroup() {
        return group;
    }

    public ArrayList<String> getCart() { return cart; }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setCart(ArrayList<String> cart) { this.cart = cart; }

    public void addCartItem(CartItem item) {
        cart.add(item.getId());
    }

    public void removeCartItem(CartItem item) {
        cart.remove(item.getId());
    }
}
