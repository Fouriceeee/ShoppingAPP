package org.example.config;

/**
 * 应用程序配置类
 */
public class AppConfig {
    // 文件路径
    public static final String PRODUCTS_FILE = "products.json";
    public static final String CART_FILE = "cart_items.json";
    public static final String KEYWORD_FILE = "data/keyword.json";

    // 数据目录
    public static final String DATA_DIR = "data";

    // 获取配置的端口
    public static int getPort() {
        return Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    }

    // 是否为开发环境
    public static boolean isDevelopment() {
        String env = System.getenv().getOrDefault("APP_ENV", "dev");
        return "dev".equalsIgnoreCase(env) || "development".equalsIgnoreCase(env);
    }
}
