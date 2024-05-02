package com.demo.gradle_demo.service.impl;

import com.demo.gradle_demo.pojo.Result;
import com.demo.gradle_demo.service.PhoneService;
import com.demo.gradle_demo.utils.GetPhoneUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class PhoneServiceImpl implements PhoneService {


    @Override
    public Result getPhone(String phoneNumber) {
        String str = "88888888";
        int len = phoneNumber.length();
        String jsonPhone = GetPhoneUtil.getPhone(phoneNumber + str);
        if (len == 3) {
            // 处理长度为3的情况，替换province和city为"未查询"
            return Result.success("供应商查找成功！！！",replaceUnqueried(jsonPhone));
        } else {
            return Result.success(StringEscapeUtils.unescapeJava(jsonPhone));
        }
    }

    private String replaceUnqueried(String jsonPhone) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 将JSON字符串转换为JsonNode对象
            JsonNode rootNode = objectMapper.readTree(jsonPhone);

            // 获取data节点
            JsonNode dataNode = rootNode.get("data");
            if (dataNode instanceof ObjectNode) {
                // 替换province和city字段的值为"未查询"
                ((ObjectNode) dataNode).put("province", "未查询");
                ((ObjectNode) dataNode).put("city", "未查询");
            }
            System.out.println(objectMapper.writeValueAsString(rootNode));
            // 将JsonNode对象转换回JSON字符串并返回
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            return jsonPhone; // 发生异常时返回原始的JSON字符串
        }
    }

}