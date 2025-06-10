package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.config.AppConfig;
import spark.Spark;
import org.example.api.Router;

import java.io.File;

public class App {

    /**
     * 获取GSON实例
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // 用于API响应

    public static Gson getGson() {
        return GSON;
    }

    public static void main(String[] args) {
        // 设置端口，可以从环境变量获取，或使用默认值 8080
        int port = AppConfig.getPort();
        Spark.port(port);

        // 验证data目录并设置静态文件服务
        setupStaticFileService();

        // 设置CORS
        setupCors();

        // 定义API路由
        Router.setupRoutes();

        // 启动服务器
        Spark.init();
        System.out.println("后端服务器已启动，端口：" + Spark.port());
        System.out.println("数据文件位于项目根目录的 'data' 文件夹下。");
        System.out.println("请确保前端请求的 API_BASE_URL 与后端端口匹配，例如：http://localhost:" + port + "/api");
    }

    /**
     * 设置静态文件服务
     */
    private static void setupStaticFileService() {
        String projectRoot = System.getProperty("user.dir");
        String dataDirectoryPath = projectRoot + File.separator + "data";
        File dataDir = new File(dataDirectoryPath);

        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("ERROR: 'data' directory not found at: " + dataDirectoryPath);
            System.err.println("Please ensure 'data' folder is in the backend's root directory.");
        } else {
            System.out.println("Serving static files from: " + dataDirectoryPath);
            Spark.externalStaticFileLocation(dataDirectoryPath);
        }
    }
    /**
     * 设置CORS
     */
    private static void setupCors() {
        // OPTIONS 请求处理
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

        // 全局CORS头部
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
    }



}