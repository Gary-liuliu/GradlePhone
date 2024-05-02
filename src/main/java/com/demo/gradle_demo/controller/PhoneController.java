package com.demo.gradle_demo.controller;

import com.demo.gradle_demo.pojo.Result;
import com.demo.gradle_demo.service.PhoneService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.demo.gradle_demo.anno.Number;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Validated
@RestController
@RequestMapping("/phone")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 创建线程池，这里选择固定大小的线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 查找手机号
    @RequestMapping("/get")
    public CompletableFuture<Result> getPhone(@Number String phoneNumber) {
        CompletableFuture<Result> future = new CompletableFuture<>();

        //检测
        CompletableFuture<Void> timeoutFuture = CompletableFuture.runAsync(() -> {
            try {
                // 5秒后检测
                Thread.sleep(5000);
                future.completeExceptionally(new TimeoutException("接口查找超时!!!"));
            } catch (InterruptedException ignored) {
            }
        });

        //查找
        CompletableFuture<Result> resultFuture = CompletableFuture.supplyAsync(() -> {
            //redis
            ValueOperations<String, String> option = stringRedisTemplate.opsForValue();
            String cachedPhone = option.get(phoneNumber);

            try {
                Thread.sleep(2000); // 模拟网络延迟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (cachedPhone != null) {
                return Result.success(cachedPhone);
            }

            Result phone = phoneService.getPhone(phoneNumber);
            //存redis
            option.set(phoneNumber, phone.getData(), 300, TimeUnit.SECONDS);
            return phone;
        }, executorService);

        resultFuture.thenAcceptAsync(result -> {
            future.complete(result);
            // 如果任务完成，取消超时检测
            timeoutFuture.cancel(true);
        });

        return future;
    }
}