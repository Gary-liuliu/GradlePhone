package com.demo.gradle_demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetPhoneUtil {

    public static String getPhone(String phoneNumber) {
        try {
            String urlString = "https://cx.shouji.360.cn/phonearea.php?number=" + phoneNumber;
            URL url = new URL(urlString);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法
            connection.setRequestMethod("GET");

            // 获取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 关闭连接
            connection.disconnect();

            // 输出响应内容
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // 返回空字符串或其他默认值
            return "";
        }
    }
}