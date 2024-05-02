package com.demo.gradle_demo.exception;


import com.demo.gradle_demo.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理
@RestControllerAdvice
public class GlobaExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
//        如果e.getMessage()有长度，那么就返回e.getMessage()，否则返回“操作失败”
        return Result.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"查找失败");
    }

}