package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class JsonIO {
    public static ArrayList<User> readUsersFromJson(String filePath) throws IOException {
        try(JsonReader reader = new JsonReader(new FileReader(filePath))) {
            ArrayList<User> users = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext()) {
                JsonElement jsonElement = com.google.gson.JsonParser.parseReader(reader);
                users.add(getUser(jsonElement));
            }
            reader.endArray();
            return users;
        }
    }

    public static ArrayList<Product> readProductsFromJson(String filePath) throws IOException {
        try(JsonReader reader = new JsonReader(new FileReader(filePath))) {
            ArrayList<Product> products = new ArrayList<>();
            reader.beginArray();
            while (reader.hasNext()) {
                JsonElement jsonElement = com.google.gson.JsonParser.parseReader(reader);
                products.add(getProduct(jsonElement));
            }
            reader.endArray();
            return products;
        }
    }

    private static User getUser(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        User user = new User();

        user.setId(jsonObject.get("ID").getAsLong());
        user.setName(jsonObject.get("NAME").getAsString());
        user.setEmail(jsonObject.get("E-MAIL").getAsString());
        user.setPassword(jsonObject.get("PASSWORD").getAsString());

        if(jsonObject.get("GROUP").getAsString().equals("ADMIN")) {
            user.setGroup(Group.ADMIN);
        }
        else {
            user.setGroup(Group.CUSTOMER);
        }
        return user;
    }

    private static Product getProduct(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Product product = new Product();

        product.setName(jsonObject.get("NAME").getAsString());
        product.setCategory(jsonObject.get("CATEGORY").getAsString());
        product.setInventory(jsonObject.get("INVENTORY").getAsInt());
        product.setPrice(jsonObject.get("PRICE").getAsLong());

        return product;
    }
}
