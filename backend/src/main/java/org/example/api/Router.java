package org.example.api;

import spark.Spark;

public class Router {
    /**
     * 设置API路由
     */
    public static void setupRoutes() {
        System.out.println("正在注册API路由...");

        // 产品相关路由
        Spark.get("/api/products", ProductController::getAllProducts);
        System.out.println("已注册: GET /api/products");

        // 购物车相关路由
        Spark.get("/api/cart", CartController::getCart);
        System.out.println("已注册: GET /api/cart");

        Spark.post("/api/cart", CartController::addToCart);
        System.out.println("已注册: POST /api/cart");

        Spark.patch("/api/cart/select-all", CartController::selectAllCartItems);
        System.out.println("已注册: PATCH /api/cart/select-all");

        Spark.post("/api/cart/batch-delete", CartController::batchDeleteCartItems);
        System.out.println("已注册: POST /api/cart/batch-delete");

        Spark.delete("/api/cart/clear", CartController::clearCart);
        System.out.println("已注册: DELETE /api/cart/clear");

        /*通配路由：一定要在具体路由之后注册！！否则请求会先被通配路由接收！！！！！！*/
        Spark.patch("/api/cart/:itemId", CartController::updateCartItem);
        System.out.println("已注册: PATCH /api/cart/:itemId");

        Spark.delete("/api/cart/:itemId", CartController::deleteCartItem);
        System.out.println("已注册: DELETE /api/cart/:itemId");

        System.out.println("所有API路由注册完成！");
    }

}
