package top.xinsin.http;

import java.net.http.HttpClient;
import java.time.Duration;

import static top.xinsin.util.ConstantUtils.TIMEOUT_SECONDS;

/**
 * HttpClient单例模式实现（线程安全）
 */
public class HttpClientSingleton {
    // 持有私有静态实例，防止被外部直接访问
    private static volatile HttpClient instance;

    // 私有构造方法，防止外部实例化
    private HttpClientSingleton() {
        // 阻止通过反射创建实例
        if (instance != null) {
            throw new RuntimeException("禁止通过反射创建实例");
        }
    }

    /**
     * 获取全局唯一的HttpClient实例
     * 双重检查锁（DCL）确保线程安全且高效
     */
    public static HttpClient getInstance() {
        // 第一次检查：避免不必要的同步
        if (instance == null) {
            // 同步块：保证多线程环境下的安全性
            synchronized (HttpClientSingleton.class) {
                // 第二次检查：防止多个线程同时通过第一次检查后重复创建实例
                if (instance == null) {
                    // 构建HttpClient实例
                    instance = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_2)
                            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build();
                }
            }
        }
        return instance;
    }

    /**
     * 防止反序列化重新创建实例
     */
    private Object readResolve() {
        return getInstance();
    }
}

