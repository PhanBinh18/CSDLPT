package com.example.demo.config;

public class DbContextHolder {
    private static final ThreadLocal<SiteEnum> contextHolder = new ThreadLocal<>();

    // Chuyển hướng kết nối
    public static void setCurrentDb(SiteEnum siteEnum) {
        contextHolder.set(siteEnum);
    }

    // Lấy thông tin kết nối hiện tại
    public static SiteEnum getCurrentDb() {
        return contextHolder.get();
    }

    // Dọn dẹp sau khi dùng xong để tránh tràn bộ nhớ (Memory Leak)
    public static void clear() {
        contextHolder.remove();
    }
}