package org.example.model;

import java.util.LinkedHashMap;

public class User {
    private long id;
    private String name;
    private String picture;
    private String email;
    private String password;

    private final boolean admin;

    //String:id of lProduct
    //Integer:quantity of lProduct
    //the sign of Integer:selected(+ : true, - : false)
    private LinkedHashMap<Long,Integer> cartMap;

    public User(long id,String name,String picture,String email,String password,boolean admin){
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.cartMap = new LinkedHashMap<>();
    }

    public User(long id,String name,String picture,String email,String password,boolean admin,LinkedHashMap<Long,Integer> cartMap){
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.cartMap = cartMap;
    }
    //Standard Getter & Setter
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getPicture() {return picture;}
    public void setPicture(String picture) {this.picture = picture;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public boolean isAdmin() {return admin;}
    public LinkedHashMap<Long, Integer> getCartMap() {return cartMap;}
    public void setCartMap(LinkedHashMap<Long, Integer> cartMap) {this.cartMap = cartMap;}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", admin='" + admin + '\'' +
                ", cartMap='" + cartMap + '\'' +
                '}';
    }

    public void addProductToCart(long id,int quantity) {
        this.cartMap.put(id, quantity);
    }

    //返回this的拷贝
    public User copy() {
        return new User(id,name,picture,email,password,admin,cartMap);
    }

    //从newUser拷贝除id,admin外的内容到this
    public void copyFrom(User newUser) {
        name = newUser.getName();
        picture = newUser.getPicture();
        email = newUser.getEmail();
        password = newUser.getPassword();
        cartMap = newUser.getCartMap();
    }
}
