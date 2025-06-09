package org.example.api;

import org.example.model.Category;
import org.example.util.ApiResponseUtil;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类API控制器
 */
public class CategoryController {

    /**
     * 获取所有商品分类
     */
    public static Object getAllCategories(Request req, Response res) {
        res.type("application/json");

        try {
            List<String> categories = Arrays.stream(Category.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());

            System.out.println("DEBUG: 成功获取" + categories.size() + "个商品分类。");
            return ApiResponseUtil.success("商品分类获取成功", categories);
        } catch (Exception e) {
            return ApiResponseUtil.serverError(res, "获取商品分类失败", e);
        }
    }
}
