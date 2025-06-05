package org.example.api;

import com.google.gson.Gson;
import org.example.*;
import org.example.config.AppConfig;
import org.example.model.Product;
import org.example.repository.JsonIO;
import org.example.util.ApiResponseUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

/**
 * 产品API控制器
 */
public class ProductController {
    private static final String PRODUCTS_FILE = AppConfig.PRODUCTS_FILE;
    private static final Gson GSON = App.getGson();

    /**
     * 获取所有产品
     */
    public static Object getAllProducts(Request req, Response res) {
        res.type("application/json");

        try {
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
            System.out.println("DEBUG: Fetched " + products.size() + " products.");
            return ApiResponseUtil.success("Products fetched successfully.", products);
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Failed to read products data", e);
        }
    }
}
