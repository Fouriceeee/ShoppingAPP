package org.example.util;

import org.example.config.AppConfig;
import org.example.model.Product;
import org.example.repository.JsonIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchUtil {
    private static final String PRODUCTS_FILE = AppConfig.PRODUCTS_FILE;
    private static final String KEYWORD_FILE = AppConfig.KEYWORD_FILE;

    public static ArrayList<String> search(String searchString) throws IOException {
        String search = searchString.toLowerCase();
        System.out.println("DEBUG: search: Searching for " + search);

        List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
        System.out.println("DEBUG:search: products的内容：" + products.size());
        Map<String, List<String>> map = JsonIO.readKeywords(KEYWORD_FILE);
        System.out.println("DEBUG:search: map的内容：" + map.size());

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
