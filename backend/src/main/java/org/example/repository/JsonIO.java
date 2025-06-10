package org.example.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken; // 用于泛型类型，如List<CartItem>
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.config.AppConfig;
import org.example.model.CartItem;
import org.example.model.Product;
import org.example.model.User;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonIO {

    // 使用 ReentrantReadWriteLock 确保在多线程环境下文件读写的安全
    // 读写锁允许多个线程同时读，但只允许一个线程写。
    private static final ReadWriteLock cartFileLock = new ReentrantReadWriteLock();
    // 可以为不同的文件定义不同的锁，或共享一个锁但要小心死锁

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_DIR = AppConfig.DATA_DIR; // 数据文件夹名，相对于项目根目录

    // --- 辅助方法：获取文件路径 ---
    private static File getDataFile(String fileName) {
        // 构建文件路径：项目根目录/data/fileName
        String projectRoot = System.getProperty("user.dir");
        return new File(projectRoot + File.separator + DATA_DIR + File.separator + fileName);
    }

    // --- 通用 JSON 列表读取方法 ---
    private static <T> List<T> readJsonList(String fileName, Type typeToken) throws IOException {
        File file = getDataFile(fileName);
        if (!file.exists()) {
            // 如果文件不存在，尝试创建父目录和空文件，写入空数组
            file.getParentFile().mkdirs(); // 确保父目录存在
            file.createNewFile();
            try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                GSON.toJson(new ArrayList<T>(), typeToken, writer); // 写入一个空的JSON数组
            }
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            return GSON.fromJson(reader, typeToken);
        }
    }

    // --- 通用 JSON 列表写入方法 ---
    private static <T> void writeJsonList(String fileName, List<T> list, Type typeToken) throws IOException {
        File file = getDataFile(fileName);
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            GSON.toJson(list, typeToken, writer);
        }
    }

    // --- CartItem IO 方法 ---
    public static List<CartItem> readCartItems(String fileName) throws IOException {
        cartFileLock.readLock().lock(); // 获取读锁
        try {
            // 使用 TypeToken 来表示 List<CartItem> 的泛型类型
            Type listType = new TypeToken<ArrayList<CartItem>>() {}.getType();
            return readJsonList(fileName, listType);
        } finally {
            cartFileLock.readLock().unlock(); // 释放读锁
        }
    }

    public static void writeCartItems(String fileName, List<CartItem> cartItems) throws IOException {
        cartFileLock.writeLock().lock(); // 获取写锁
        try {
            Type listType = new TypeToken<ArrayList<CartItem>>() {}.getType();
            writeJsonList(fileName, cartItems, listType);
        } finally {
            cartFileLock.writeLock().unlock(); // 释放写锁
        }
    }

    // UserIO
    public static List<User> readUsers(String fileName) throws IOException {
        Type listType = new TypeToken<ArrayList<User>>() {}.getType();
        return readJsonList(fileName, listType);
    }

    public static void writeUsers(String fileName, List<User> users) throws IOException {
        Type listType = new TypeToken<ArrayList<User>>() {}.getType();
        writeJsonList(fileName, users, listType);
    }

    // ProductIO
    public static List<Product> readProducts(String fileName) throws IOException {
        Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
        return readJsonList(fileName, listType);
    }

    public static void writeProducts(String fileName, List<Product> products) throws IOException {
        Type listType = new TypeToken<ArrayList<Product>>() {}.getType();
        writeJsonList(fileName, products, listType);
    }

    public static Map<String,List<String>> readKeywords(String keywordFile) throws IOException {
        try(JsonReader reader = new JsonReader(new FileReader(keywordFile))) {
            return GSON.fromJson(reader,Map.class);
        }
    }

    public static Product[] testReadProducts(String fileName) throws IOException{
        try(JsonReader reader = new JsonReader(new FileReader(fileName))) {
            return GSON.fromJson(reader,Product[].class);
        }
    }
}