package org.example;

import org.example.model.Product;

import java.util.ArrayList;

public class Search {

    //遍历product[],将title含有searchString子字符串的product添加到results中
    public static ArrayList<Product> searchProduct(Product[] products, String searchString) {
        ArrayList<Product> results = new ArrayList<>();
        for(Product product:products) {
            if(product.getTitle().toLowerCase().contains(searchString.toLowerCase()))
                results.add(product);
        }
        return results;
    }
}
