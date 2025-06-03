package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject; // 用于解析通用 JSON 对象
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class App {

    private static final String CART_FILE = "cart_items.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // 用于API响应

    public static void main(String[] args) {
        // 设置端口，可以从环境变量获取，或使用默认值 8080
        // 前端 API_BASE_URL 的端口是 5173，这里为了匹配前端可能需要调整
        // 或者前端修改为访问 8080 端口，这里假设后端运行在 8080
        Spark.port(8080);

        String projectRoot = System.getProperty("user.dir");
        String dataDirectoryPath = projectRoot + File.separator + "data";
        File dataDir = new File(dataDirectoryPath);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("ERROR: 'data' directory not found at: " + dataDirectoryPath);
            System.err.println("Please ensure 'data' folder is in the backend's root directory.");
        } else {
            System.out.println("Serving static files from: " + dataDirectoryPath);
            // 使用 externalStaticFileLocation 将文件系统中的目录暴露为静态资源
            // 访问 http://localhost:8080/images/product_a.jpg 会去查找 dataDirectoryPath/images/product_a.jpg
            Spark.externalStaticFileLocation(dataDirectoryPath);
        }

        // 设置 CORS 头部，允许所有来源访问（开发环境常用，生产环境请根据实际需求配置）
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization"); // 允许的请求头
        });

        // --- API 路由定义 ---

        // PATCH /api/cart/select-all - 更新购物车中所有商品的选中状态（全选/全不选）
        Spark.patch("/api/cart/select-all", (req, res) -> {
            res.type("application/json");
            String requestBodyString = req.body(); // 获取原始请求体字符串
            System.out.println("DEBUG: Received PATCH /api/cart/select-all request. Raw Body: '" + requestBodyString + "'"); // 打印原始请求体

            try {
                JsonObject requestBody = GSON.fromJson(requestBodyString, JsonObject.class);
                if (requestBody == null || !requestBody.has("selected") || !requestBody.get("selected").isJsonPrimitive()) {
                    System.err.println("ERROR: Validation failed: Invalid request body for select-all. Body was: '" + requestBodyString + "'");
                    Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid request body. Expected { selected: boolean }.")));
                    return null;
                }
                boolean selectAll = requestBody.get("selected").getAsBoolean();

                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                cartItems.forEach(item -> item.setSelected(selectAll)); // 更新所有商品的选中状态
                JsonIO.writeCartItems(CART_FILE, cartItems);
                return GSON.toJson(new ApiResponse(200, "All cart items selection updated to " + selectAll + "."));
            } catch (com.google.gson.JsonSyntaxException e) { // 更具体的 JSON 解析错误捕获
                System.err.println("ERROR: JSON syntax error for select-all: " + e.getMessage() + ". Original body: '" + requestBodyString + "'");
                Spark.halt(400, GSON.toJson(new ApiResponse(400, "JSON syntax error in request body: " + e.getMessage())));
                return null;
            } catch (Exception e) {
                System.err.println("ERROR: Other error processing select-all request: " + e.getMessage() + ". Original body: '" + requestBodyString + "'");
                Spark.halt(400, GSON.toJson(new ApiResponse(400, "Error processing request body: " + e.getMessage())));
                return null;
            }
        });

        // GET /api/cart - 获取购物车中的所有商品
        Spark.get("/api/cart", (req, res) -> {
            res.type("application/json");
            try {
                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                return GSON.toJson(cartItems);
            } catch (IOException e) {
                System.err.println("Error reading cart data: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error reading cart data: " + e.getMessage())));
                return null;
            }
        });

        // POST /api/cart - 向购物车添加一个新商品
        // 如果商品已存在，则增加数量；如果不存在，则新增
        Spark.post("/api/cart", (req, res) -> {
            res.type("application/json");
            try {
                CartItem newItem = GSON.fromJson(req.body(), CartItem.class);
                if (newItem == null || newItem.getId() == null || newItem.getId().isEmpty() || newItem.getQuantity() <= 0) {
                    Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid cart item data. Required fields: id, quantity (must be > 0).")));
                    return null;
                }

                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                boolean found = false;
                for (CartItem item : cartItems) {
                    if (item.getId().equals(newItem.getId())) {
                        // 如果商品已存在，更新数量和最新信息（例如价格、图片等）
                        item.setQuantity(item.getQuantity() + newItem.getQuantity());
                        item.setImage(newItem.getImage());
                        item.setTitle(newItem.getTitle());
                        item.setPriceInteger(newItem.getPriceInteger());
                        item.setPriceDecimal(newItem.getPriceDecimal());
                        // 选中状态由前端决定，这里可以保持或更新
                        // item.setSelected(newItem.isSelected());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // 如果商品不存在，则添加新商品
                    cartItems.add(newItem);
                }
                JsonIO.writeCartItems(CART_FILE, cartItems);
                // 返回更新后的购物车项，或整个列表，这里返回添加/更新的项
                return GSON.toJson(newItem);
            } catch (IOException e) {
                System.err.println("Error processing addCartItem request: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error processing cart request: " + e.getMessage())));
                return null;
            } catch (Exception e) {
                System.err.println("Invalid JSON or request body for addCartItem: " + e.getMessage());
                Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid JSON or request body: " + e.getMessage())));
                return null;
            }
        });

        // PATCH /api/cart/:itemId - 更新购物车中指定商品的属性（如数量、选中状态）
        Spark.patch("/api/cart/:itemId", (req, res) -> {
            res.type("application/json");
            String itemId = req.params(":itemId");
            try {
                JsonObject updateData = GSON.fromJson(req.body(), JsonObject.class);
                if (updateData == null) {
                    Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid update data.")));
                    return null;
                }

                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                Optional<CartItem> existingItemOpt = cartItems.stream()
                        .filter(item -> item.getId().equals(itemId))
                        .findFirst();

                if (existingItemOpt.isPresent()) {
                    CartItem itemToUpdate = existingItemOpt.get();

                    if (updateData.has("quantity")) {
                        int newQuantity = updateData.get("quantity").getAsInt();
                        if (newQuantity < 0) { // 数量不能为负
                            Spark.halt(400, GSON.toJson(new ApiResponse(400, "Quantity cannot be negative.")));
                            return null;
                        }
                        if (newQuantity == 0) {
                            // 如果数量为0，则删除该商品
                            cartItems.remove(itemToUpdate);
                            JsonIO.writeCartItems(CART_FILE, cartItems);
                            return GSON.toJson(new ApiResponse(200, "Product removed due to quantity 0."));
                        } else {
                            itemToUpdate.setQuantity(newQuantity);
                        }
                    }
                    if (updateData.has("selected")) {
                        itemToUpdate.setSelected(updateData.get("selected").getAsBoolean());
                    }
                    // 如果还有其他字段需要更新，可以在这里添加逻辑
                    // 例如：if (updateData.has("priceInteger")) itemToUpdate.setPriceInteger(updateData.get("priceInteger").getAsString());

                    JsonIO.writeCartItems(CART_FILE, cartItems);
                    return GSON.toJson(itemToUpdate);
                } else {
                    Spark.halt(404, GSON.toJson(new ApiResponse(404, "Product not found in cart.")));
                    return null;
                }
            } catch (IOException e) {
                System.err.println("Error updating cart item: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error updating cart data: " + e.getMessage())));
                return null;
            } catch (Exception e) {
                System.err.println("Invalid JSON or request body for updateCartItem: " + e.getMessage());
                Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid JSON or request body: " + e.getMessage())));
                return null;
            }
        });

        // DELETE /api/cart/:itemId - 从购物车中删除指定商品
        Spark.delete("/api/cart/:itemId", (req, res) -> {
            res.type("application/json");
            String itemId = req.params(":itemId");
            try {
                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                boolean removed = cartItems.removeIf(item -> item.getId().equals(itemId));
                if (removed) {
                    JsonIO.writeCartItems(CART_FILE, cartItems);
                    return GSON.toJson(new ApiResponse(200, "Product removed from cart successfully."));
                } else {
                    Spark.halt(404, GSON.toJson(new ApiResponse(404, "Product not found in cart.")));
                    return null;
                }
            } catch (IOException e) {
                System.err.println("Error deleting cart item: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error deleting cart item: " + e.getMessage())));
                return null;
            }
        });

        // POST /api/cart/batch-delete - 批量删除购物车中的商品
        Spark.post("/api/cart/batch-delete", (req, res) -> {
            res.type("application/json");
            try {
                // 前端发送 { ids: ["id1", "id2"] }
                JsonObject requestBody = GSON.fromJson(req.body(), JsonObject.class);
                if (requestBody == null || !requestBody.has("ids") || !requestBody.get("ids").isJsonArray()) {
                    Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid request body. Expected { ids: [] }.")));
                    return null;
                }
                List<String> itemIdsToDelete = GSON.fromJson(requestBody.get("ids"), ArrayList.class);

                List<CartItem> cartItems = JsonIO.readCartItems(CART_FILE);
                int initialSize = cartItems.size();
                // 移除列表中id在itemIdsToDelete中的所有项
                cartItems.removeIf(item -> itemIdsToDelete.contains(item.getId()));

                JsonIO.writeCartItems(CART_FILE, cartItems);
                int removedCount = initialSize - cartItems.size();
                return GSON.toJson(new ApiResponse(200, removedCount + " items removed successfully."));
            } catch (IOException e) {
                System.err.println("Error batch deleting cart items: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error batch deleting cart items: " + e.getMessage())));
                return null;
            } catch (Exception e) {
                System.err.println("Invalid JSON for batch delete: " + e.getMessage());
                Spark.halt(400, GSON.toJson(new ApiResponse(400, "Invalid JSON for batch delete: " + e.getMessage())));
                return null;
            }
        });

        // DELETE /api/cart/clear - 清空购物车中的所有商品
        Spark.delete("/api/cart/clear", (req, res) -> {
            res.type("application/json");
            try {
                JsonIO.writeCartItems(CART_FILE, new ArrayList<>()); // 写入一个空列表
                return GSON.toJson(new ApiResponse(200, "All cart items cleared successfully."));
            } catch (IOException e) {
                System.err.println("Error clearing cart items: " + e.getMessage());
                Spark.halt(500, GSON.toJson(new ApiResponse(500, "Error clearing cart items: " + e.getMessage())));
                return null;
            }
        });




        // 启动服务器后的回调
        Spark.init();
        System.out.println("购物车API服务器已启动，端口：" + Spark.port());
        System.out.println("数据文件位于项目根目录的 'data' 文件夹下。");
        System.out.println("请确保前端请求的 API_BASE_URL 与后端端口匹配，例如：http://localhost:8080/api");
    }

    // 用于统一API响应格式的内部类
    private static class ApiResponse {
        private int code;
        private String message;
        private Object data; // 可以包含额外数据

        public ApiResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ApiResponse(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}