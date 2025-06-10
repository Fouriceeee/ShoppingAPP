package org.example.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.example.*;
import org.example.config.AppConfig;
import org.example.model.CartItem;
import org.example.model.Product;
import org.example.repository.JsonIO;
import org.example.util.ApiResponseUtil;
import org.example.util.LoggerUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 购物车API控制器
 */
public class CartController {
    private static final String CART_FILE = AppConfig.CART_FILE;
    private static final String PRODUCTS_FILE = AppConfig.PRODUCTS_FILE;
    private static final Gson GSON = App.getGson();

    /**
     * 获取购物车中的所有商品
     */
    public static Object getCart(Request req, Response res) {
        res.type("application/json");
        try {
            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
            return GSON.toJson(cartItems);
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error reading cart data", e);
        }
    }

    /**
     * 向购物车添加商品
     */
    public static Object addToCart(Request req, Response res) {
        res.type("application/json");
        String requestBodyString = req.body();

        try {
            CartItem cartItem = GSON.fromJson(requestBodyString, CartItem.class);

            if(cartItem == null || !cartItem.isValid()){
                LoggerUtil.error("Invalid add to cart request data. Body: " + requestBodyString);
                return ApiResponseUtil.clientError(res, 400, "Invalid add to cart data. Required fields: id, quantity (must be > 0).");
            }

            String id = cartItem.getId();
            int quantity = cartItem.getQuantity();

            // 1. 从products.json中查找对应的产品信息
            List<Product> products = JsonIO.readProducts(PRODUCTS_FILE);
            Optional<Product> productToAddOpt = products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();

            if (productToAddOpt.isEmpty()) {
                return ApiResponseUtil.clientError(res, 404, "Product not found with ID: " + id);
            }

            Product productData = productToAddOpt.get();

            // 2. 读取当前购物车内容
            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
            CartItem updatedOrNewCartItem = null;
            boolean foundInCart = false;

            // 3. 检查购物车中是否已存在该商品
            for (CartItem item : cartItems) {
                if (item.getId().equals(id)) {
                    // 更新已存在的商品
                    item.copyFrom(productData,item.getQuantity()+quantity,true);
                    updatedOrNewCartItem = item;
                    foundInCart = true;
                    break;
                }
            }

            // 4. 如果购物车中不存在，则添加新商品
            if (!foundInCart) {
                updatedOrNewCartItem = productData.toCartItem(quantity,true);
                cartItems.add(updatedOrNewCartItem);
            }

            // 5. 保存购物车
            JsonIO.writeCartItems(CART_FILE, cartItems);

            return ApiResponseUtil.success("Item added/updated in cart successfully.", updatedOrNewCartItem);
        } catch (JsonSyntaxException e) {
            return ApiResponseUtil.clientError(res, 400, "JSON syntax error in request body: " + e.getMessage());
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error processing cart request", e);
        } catch (Exception e) {
            return ApiResponseUtil.serverError(res, "An unexpected error occurred", e);
        }
    }

    /**
     * 更新购物车中指定商品的属性
     */
    public static Object updateCartItem(Request req, Response res) {
        res.type("application/json");
        String itemId = req.params(":itemId");

        try {
            JsonObject updateData = GSON.fromJson(req.body(), JsonObject.class);
            if (updateData == null) {
                return ApiResponseUtil.clientError(res, 400, "Invalid update data.");
            }

            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
            Optional<CartItem> existingItemOpt = cartItems.stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst();

            if (existingItemOpt.isEmpty()) {
                return ApiResponseUtil.clientError(res, 404, "Product not found in cart.");
            }

            CartItem itemToUpdate = existingItemOpt.get();

            if (updateData.has("quantity")) {
                int newQuantity = updateData.get("quantity").getAsInt();
                if (newQuantity < 0) {
                    return ApiResponseUtil.clientError(res, 400, "Quantity cannot be negative.");
                }
                if (newQuantity == 0) {
                    cartItems.remove(itemToUpdate);
                    JsonIO.writeCartItems(CART_FILE, cartItems);
                    return ApiResponseUtil.success("Product removed due to quantity 0.");
                }
                itemToUpdate.setQuantity(newQuantity);
            }

            if (updateData.has("selected")) {
                itemToUpdate.setSelected(updateData.get("selected").getAsBoolean());
            }

            JsonIO.writeCartItems(CART_FILE, cartItems);
            return GSON.toJson(itemToUpdate);

        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error updating cart data", e);
        } catch (Exception e) {
            return ApiResponseUtil.clientError(res, 400, "Invalid JSON or request body: " + e.getMessage());
        }
    }

    /**
     * 删除购物车中的指定商品
     */
    public static Object deleteCartItem(Request req, Response res) {
        res.type("application/json");
        String itemId = req.params(":itemId");

        try {
            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
            boolean removed = cartItems.removeIf(item -> item.getId().equals(itemId));

            if (removed) {
                JsonIO.writeCartItems(CART_FILE, cartItems);
                return ApiResponseUtil.success("Product removed from cart successfully.");
            } else {
                return ApiResponseUtil.clientError(res, 404, "Product not found in cart.");
            }
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error deleting cart item", e);
        }
    }

    /**
     * 批量删除购物车中的商品
     */
    public static Object batchDeleteCartItems(Request req, Response res) {
        res.type("application/json");

        try {
            JsonObject requestBody = GSON.fromJson(req.body(), JsonObject.class);
            if (requestBody == null || !requestBody.has("ids") || !requestBody.get("ids").isJsonArray()) {
                return ApiResponseUtil.clientError(res, 400, "Invalid request body. Expected { ids: [] }.");
            }

            List<String> itemIdsToDelete = GSON.fromJson(requestBody.get("ids"), ArrayList.class);
            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
            int initialSize = cartItems.size();

            cartItems.removeIf(item -> itemIdsToDelete.contains(item.getId()));
            JsonIO.writeCartItems(CART_FILE, cartItems);

            int removedCount = initialSize - cartItems.size();
            return ApiResponseUtil.success(removedCount + " items removed successfully.");
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error batch deleting cart items", e);
        } catch (Exception e) {
            return ApiResponseUtil.clientError(res, 400, "Invalid JSON for batch delete: " + e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    public static Object clearCart(Request req, Response res) {
        res.type("application/json");

        try {
            JsonIO.writeCartItems(CART_FILE, new ArrayList<>());
            return ApiResponseUtil.success("All cart items cleared successfully.");
        } catch (IOException e) {
            return ApiResponseUtil.serverError(res, "Error clearing cart items", e);
        }
    }

    /**
     * 更新购物车中所有商品的选中状态
     */
    public static Object selectAllCartItems(Request req, Response res) {
        /*System.out.println("=== selectAllCartItems 被调用 ===");
        System.out.println("请求方法: " + req.requestMethod());
        System.out.println("请求路径: " + req.pathInfo());
        System.out.println("Content-Type: " + req.contentType());*/

        res.type("application/json");
        String requestBodyString = req.body();
        System.out.println("DEBUG: selectAllCartItems: Received request body: " + requestBodyString);

        try {
            JsonObject requestBody = GSON.fromJson(requestBodyString, JsonObject.class);
            if (requestBody == null || !requestBody.has("selected") || !requestBody.get("selected").isJsonPrimitive()) {
                return ApiResponseUtil.clientError(res, 400, "Invalid request body. Expected { selected: boolean }.");
            }

            boolean selectAll = requestBody.get("selected").getAsBoolean();
            List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);

            cartItems.forEach(item -> item.setSelected(selectAll));
            JsonIO.writeCartItems(CART_FILE, cartItems);

            return ApiResponseUtil.success("All cart items selection updated to " + selectAll + ".");
        } catch (JsonSyntaxException e) {
            return ApiResponseUtil.clientError(res, 400, "JSON syntax error in request body: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponseUtil.clientError(res, 400, "Error processing request body: " + e.getMessage());
        }
    }
}
