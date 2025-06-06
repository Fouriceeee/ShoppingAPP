package org.example.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.*;
import org.example.config.AppConfig;
import org.example.model.CartItem;
import org.example.model.Category;
import org.example.model.Product;
import org.example.repository.JsonIO;
import org.example.util.ApiResponseUtil;
import org.example.util.LoggerUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;
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

        try {
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
            Product foundProduct = null;

            for (Product product : products) {
                if (product.getId().equals(productId)) {
                    foundProduct = product;
                    break;
                }
            }

            if (foundProduct != null) {
                System.out.println("DEBUG: Found product with ID: " + productId);
                return ApiResponseUtil.success("产品获取成功", foundProduct);
            } else {
                res.status(404);
                return ApiResponseUtil.clientError(res, 404, "未找到ID为" + productId + "的产品");
            }
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "获取产品数据失败", e);
        }
    }

    public static Object addToProducts(Request req, Response res) {
        res.type("application/json");
        String requestBodyString = req.body();

        try{
            AddToProductsRequest addRequest = GSON.fromJson(requestBodyString, AddToProductsRequest.class);

            if(addRequest == null || !addRequest.isValid()) {
                LoggerUtil.error("Invalid add to products request data. Body: " + requestBodyString);
                return ApiResponseUtil.clientError(res, 400, "Invalid add to products data. Required fields: id, priceInteger, priceDecimal, category.");
            }

            String id = addRequest.getId();
            String image = addRequest.getImage();
            String title = addRequest.getTitle();
            int priceInteger = addRequest.getPriceInteger();
            int priceDecimal = addRequest.getPriceDecimal();
            String category = addRequest.getCategory();
            String description = addRequest.getDescription();

            //1.从products.json中查找是否有相同ID的商品
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
            Optional<Product> productToAddOpt = products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();

            Product productData = null;

            //2.检查商品列表中是否已存在该商品
            if (productToAddOpt.isPresent()) {
                // 更新已存在的商品
                productData = productToAddOpt.get();
                productData.setImage(image);
                productData.setTitle(title);
                productData.setPriceInteger(String.valueOf(priceInteger));
                productData.setPriceDecimal(String.valueOf(priceDecimal));
                productData.setCategory(Category.valueOf(category));
                productData.setDescription(description);
            }else{
                //添加新商品
                productData = new Product(
                        id,
                        image,
                        title,
                        String.valueOf(priceInteger),
                        String.valueOf(priceDecimal),
                        Category.valueOf(category),
                        description
                );
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
     * 商品添加请求
     */
    private static class AddToProductsRequest {
        private String id;
        private String image;
        private String title;
        private int priceInteger;
        private int priceDecimal;
        private String category;
        private String description;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public int getPriceInteger() { return priceInteger; }
        public void setPriceInteger(int priceInteger) { this.priceInteger = priceInteger; }
        public int getPriceDecimal() { return priceDecimal; }
        public void setPriceDecimal(int priceDecimal) { this.priceDecimal = priceDecimal; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public String toString() {
            return "AddToProductRequest{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", priceInteger='" + priceInteger + '\'' +
                    ", priceDecimal='" + priceDecimal + '\'' +
                    ", category='" + category + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        public Boolean isValid() {
            return (id != null && !id.isEmpty() && priceInteger >= 0 && priceDecimal >= 0 && priceDecimal <= 99 && Category.containsEnumValue(Category.class,category));
        }
    }
}
