package org.example.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志工具类
 */
public class LoggerUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 输出INFO级别日志
     */
    public static void info(String message) {
        System.out.println(getTimestamp() + " [INFO] " + message);
    }

    /**
     * 输出DEBUG级别日志
     */
    public static void debug(String message) {
        System.out.println(getTimestamp() + " [DEBUG] " + message);
    }

    /**
     * 输出ERROR级别日志
     */
    public static void error(String message) {
        System.err.println(getTimestamp() + " [ERROR] " + message);
    }

    /**
     * 输出ERROR级别日志，包含异常
     */
    public static void error(String message, Throwable e) {
        System.err.println(getTimestamp() + " [ERROR] " + message + ": " + e.getMessage());
    }

    /**
     * 获取当前时间戳
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
