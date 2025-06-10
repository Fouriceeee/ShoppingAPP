package org.example.api;

import org.example.config.AppConfig;
import org.example.model.Product;
import org.example.repository.JsonIO;
import org.example.util.ApiResponseUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchController {
    private static final String PRODUCTS_FILE = AppConfig.PRODUCTS_FILE;
    private static final String KEYWORD_FILE = AppConfig.KEYWORD_FILE;

    /**
     * 根据搜索字符串，在产品数据中查找匹配的产品ID.
     */
    public static Object searchProducts(Request req, Response res) throws IOException{
        res.type("application/json");
        String searchString = req.queryParams("query");
        System.out.println("DEBUG:searchProducts:开始搜索关键词为 '" + searchString + "' 的商品。");

        String search = searchString.toLowerCase();
        System.out.println("DEBUG: search: Searching for " + search);

        List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
        System.out.println("DEBUG:search: products的内容：" + products.size());
        Map<String, List<String>> map = JsonIO.readKeywords(KEYWORD_FILE);
        System.out.println("DEBUG:search: map的内容：" + map.size());

        ArrayList<Product> results = new ArrayList<>();

        for (Product product:products) {
            if (product.getTitle().toLowerCase().contains(search))
                results.add(product);
            else{
                for (String keyword:map.get(product.getCategory().name())) {
                    if (keyword.toLowerCase().contains(search)) {
                        results.add(product);
                        break;
                    }
                }
            }
        }

        return ApiResponseUtil.success("Search successfully.", results);
    }
}