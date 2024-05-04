package com.demo.gradle_demo.controller;

import com.demo.gradle_demo.pojo.CachePhone;
import com.demo.gradle_demo.pojo.Result;
import com.demo.gradle_demo.service.PhoneService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Recover;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import com.demo.gradle_demo.anno.Number;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;


@Validated
@RestController
@RequestMapping("/phone")
public class PhoneController {

    @Autowired
    private PhoneService phoneService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private String phoneNumber;

    // 查找手机号，添加重试机制
    @GetMapping("/get")
    @Retryable(value = TimeoutException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public CompletableFuture<Result> getPhone(@Number String phoneNumber) {
        CompletableFuture<Result> future = new CompletableFuture<>();

        this.phoneNumber = phoneNumber;
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

            // 模拟网络延迟
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (cachedPhone != null) {
                System.out.println(cachedPhone);
                return Result.success(cachedPhone);
            }

            Result phone = phoneService.getPhone(phoneNumber);
            if (phone.getCode() == 0) {
                // 存储手机号到Redis
                option.set(phoneNumber, (String) phone.getData(), 300, TimeUnit.SECONDS);

                // 获取 Redis 有序集合操作对象
                ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

                // 分值设为当前时间戳
                zSetOps.add("recent_set", phoneNumber, System.currentTimeMillis());

                // 保留最新的六个手机号
                zSetOps.removeRange("recent_set", 0, -6);
            }


            return phone;
        });

        resultFuture.thenAcceptAsync(result -> {
            future.complete(result);
            // 如果任务完成，取消超时检测
            timeoutFuture.cancel(true);
        });

        return future;
    }

    @GetMapping()
    public Result<List<CachePhone>> getHistory() {
        // 获取 Redis 有序集合操作对象
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        // 从 Redis 中获取最近的手机号集合，按照分值排序
        Set<String> recentPhoneNumbers = zSetOps.range("recent_set", 0, -1);

        // 根据手机号从缓存中获取详细信息
        List<CachePhone> history = new ArrayList<>();
        if (recentPhoneNumbers != null) {
            for (String phoneNumber : recentPhoneNumbers) {
                String cachedPhone = stringRedisTemplate.opsForValue().get(phoneNumber);
                if (cachedPhone != null) {
                    CachePhone cachePhone = new CachePhone(phoneNumber, cachedPhone);
                    history.add(cachePhone);
                }
            }
        }

        return Result.success(history);
    }


}