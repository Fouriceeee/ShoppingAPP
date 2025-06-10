package org.example.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.*;
import org.example.config.AppConfig;
import org.example.model.Product;
import org.example.repository.JsonIO;
import org.example.util.ApiResponseUtil;
import org.example.util.LoggerUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    /**
     * 根据ID获得对应产品
     */
    public static Object getProductById(Request req, Response res) {
        res.type("application/json");
        String productId = req.params(":productId");
        System.out.println("DEBUG：开始查找ID为" + productId + "的产品");

        try {
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);

            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    System.out.println("DEBUG: 成功找到ID为 " + productId + " 的产品");
                    return ApiResponseUtil.success("产品获取成功", product);
                }
            }
            res.status(404);
            return ApiResponseUtil.clientError(res, 404, "未找到ID为" + productId + "的产品");
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "获取产品数据失败", e);
        }
    }

    public static Object addToProducts(Request req, Response res) {
        res.type("application/json");
        String requestBodyString = req.body();

        try{
            Product product = GSON.fromJson(requestBodyString,Product.class);
            if(product == null || !product.isValid()){
                LoggerUtil.error("Invalid add to products request data. Body: " + requestBodyString);
                return ApiResponseUtil.clientError(res, 400, "Invalid add to products data. Required fields: id, priceInteger, priceDecimal, category.");
            }

            //1.从products.json中查找是否有相同ID的商品
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
            Optional<Product> productToAddOpt = products.stream()
                    .filter(p -> p.getId().equals(product.getId()))
                    .findFirst();

            Product productData;

            //2.检查商品列表中是否已存在该商品
            if (productToAddOpt.isPresent()) {
                // 更新已存在的商品
                productData = productToAddOpt.get();
                productData.copyFrom(product);
            }else{
                //添加新商品
                productData = product.copy();
                products.add(productData);
            }

            //3. 保存商品
            JsonIO.writeProducts(PRODUCTS_FILE, products);

            return ApiResponseUtil.success("Item added/updated in cart successfully.", productData);
        } catch (IllegalArgumentException e) {
            return ApiResponseUtil.clientError(res, 400, "Invalid Category.");
        } catch (JsonSyntaxException e) {
            return ApiResponseUtil.clientError(res, 400, "JSON syntax error in request body: " + e.getMessage());
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error processing product request", e);
        } catch (Exception e) {
            return ApiResponseUtil.serverError(res, "An unexpected error occurred", e);
        }
    }


    /**
     * 更新商品信息（管理员功能）
     */
    public static Object updateProduct(Request req, Response res) {
        res.type("application/json");
        String productId = req.params(":productId");
        String requestBodyString = req.body();

        System.out.println("DEBUG: updateProduct: 开始更新ID为" + productId + "的商品");

        try {
            // 解析请求体
            Product product = GSON.fromJson(requestBodyString,Product.class);
            if(product == null || !product.isValid()){
                LoggerUtil.error("无效的商品更新数据。请求体：" + requestBodyString);
                return ApiResponseUtil.clientError(res, 400, "无效的商品更新数据");
            }

            // 读取所有商品
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);

            // 查找要更新的商品
            for (Product existingProduct:products) {
                if (existingProduct.getId().equals(productId)) {
                    // 更新商品字段（仅更新非空字段）
                    existingProduct.copyFrom(product);

                    // 保存更新后的商品列表
                    JsonIO.writeProducts(PRODUCTS_FILE, products);

                    System.out.println("DEBUG: 成功更新ID为" + productId + "的商品");
                    return ApiResponseUtil.success("商品更新成功", existingProduct);
                }
            }
            res.status(404);
            return ApiResponseUtil.clientError(res, 404, "未找到ID为" + productId + "的商品");
        }catch (IllegalArgumentException e) {
            return ApiResponseUtil.clientError(res, 400, "无效的商品分类: ");
        } catch (JsonSyntaxException e) {
            return ApiResponseUtil.clientError(res, 400, "请求体JSON格式错误: " + e.getMessage());
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "处理商品更新请求时发生错误", e);
        } catch (Exception e) {
            return ApiResponseUtil.serverError(res, "发生意外错误", e);
        }
    }

}
