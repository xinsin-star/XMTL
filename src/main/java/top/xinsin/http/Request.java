package top.xinsin.http;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static top.xinsin.util.ConstantUtils.TIMEOUT_SECONDS;

public class Request {
    public static JSONObject getToJSONObject(String url, JSONObject params) {
        try {
            if (params != null) {
                url = url + withParams(params);
            }
            HttpClient httpClient = HttpClientSingleton.getInstance();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() == 200) {
                return JSONObject.parseObject(response.body());
            } else {
                throw new RuntimeException("GET请求失败，状态码: " + response.statusCode());
            }
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    private static String withParams(JSONObject params) {
        StringBuilder query = new StringBuilder();
            params.forEach((key, value) -> {
                try {
                    // 对参数名和值进行URL编码，避免特殊字符问题
                    String key1 = URLEncoder.encode(key, StandardCharsets.UTF_8);
                    String value2 = URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);

                    if (query.isEmpty()) {
                        query.append("?"); // 第一个参数前加?
                    } else {
                        query.append("&"); // 后续参数前加&
                    }
                    query.append(key1).append("=").append(value2);
                } catch (Exception e) {
                    throw   new RuntimeException(e);
                }
            });
        return query.toString();
    }
}
