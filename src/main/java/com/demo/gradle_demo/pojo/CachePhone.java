package com.demo.gradle_demo.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CachePhone {
    private String phoneNumber;
    private String data;
}