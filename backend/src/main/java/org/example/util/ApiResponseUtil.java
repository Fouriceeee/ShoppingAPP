package org.example.util;

import com.google.gson.Gson;
import org.example.App;
import spark.Response;

/**
 * API响应工具类，用于统一处理API响应和错误
 */
public class ApiResponseUtil {
    private static final Gson GSON = App.getGson();

    /**
     * 处理成功响应
     */
    public static String success(String message) {
        return GSON.toJson(new ApiResponse(200, message));
    }

    /**
     * 处理带数据的成功响应
     */
    public static String success(String message, Object data) {
        return GSON.toJson(new ApiResponse(200, message, data));
    }

    /**
     * 处理客户端错误
     */
    public static String clientError(Response res, int code, String message) {
        res.status(code);
        return GSON.toJson(new ApiResponse(code, message));
    }

    /**
     * 处理服务器错误
     */
    public static String serverError(Response res, String message) {
        res.status(500);
        return GSON.toJson(new ApiResponse(500, message));
    }

    /**
     * 处理带有堆栈跟踪的服务器错误
     */
    public static String serverError(Response res, String message, Exception e) {
        LoggerUtil.error(message, e);
        res.status(500);
        return GSON.toJson(new ApiResponse(500, message));
    }

    /**
     * API响应类
     */
    public static class ApiResponse {
        private int code;
        private String message;
        private Object data;

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
