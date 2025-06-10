package org.example.api;

import com.google.gson.Gson;
import org.example.App;
import org.example.util.SearchUtil; // 1. 导入 SearchUtil 类
import spark.Request;
import spark.Response;

import java.io.IOException;

public class SearchController {
    private static final Gson GSON = App.getGson();
    /**
     * 根据搜索字符串，在产品数据中查找匹配的产品ID.
     */
    public static Object searchProducts(Request req, Response res) {
        res.type("application/json");
        String searchString = req.queryParams("query");
        System.out.println("DEBUG:searchProducts:开始搜索关键词为 '" + searchString + "' 的商品。");

        try {
            return SearchUtil.search(searchString);
        } catch (IOException e) {

        }
        return null;
    }
}