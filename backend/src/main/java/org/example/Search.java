package org.example;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.example.model.Product;
import org.example.repository.JsonIO;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Search {
    public static ArrayList<String> search(String productsFile,String keywordFile,String searchString) throws IOException {
        String search = searchString.toLowerCase();
        Product[] products = JsonIO.testReadProducts(productsFile);
        Map<String, List<String>> map = JsonIO.readKeywords(keywordFile);
        ArrayList<String> results = new ArrayList<>();
        for (Product product:products) {
            if (product.getTitle().toLowerCase().contains(search))
                results.add(product.getId());
            else{
                for (String keyword:map.get(product.getCategory().name())) {
                    if (keyword.toLowerCase().contains(search)) {
                        results.add(product.getId());
                        break;
                    }
                }
            }
        }
        return results;
    }
}
