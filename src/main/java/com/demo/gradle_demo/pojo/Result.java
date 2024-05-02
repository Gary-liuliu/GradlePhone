package com.demo.gradle_demo.pojo;


//统一响应结果

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result {
    private Integer code;//业务状态码  0-成功  1-失败
    private String message;//提示信息
    private String data;//响应数据

    //快速返回操作成功响应结果(带响应数据)
    public static Result success(String data) {
        return new Result(0, "查找成功", data);
    }

    public static Result success(String message, String data) {
        return new Result(0, message, data);
    }

    public static Result error(String message) {
        return new Result(1, message, null);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}