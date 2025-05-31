package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class JsonIO {
    //UserIO
    public static User[] readUsers(String filePath) throws IOException {
        try(Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(reader,User[].class);
        }
    }

    public static void writeUsers(User[] users,String filePath) throws IOException{
        try(JsonWriter writer = new JsonWriter(new FileWriter(filePath))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users,User[].class,writer);
        }
    }

    //ProductIO
    public static Product[] readProducts(String filePath) throws IOException {
        try(Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            return gson.fromJson(reader,Product[].class);
        }
    }

    public static void writeProducts(Product[] products,String filePath) throws IOException{
        try(JsonWriter writer = new JsonWriter(new FileWriter(filePath))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(products,Product[].class,writer);
        }
    }

}
