package com.demo.gradle_demo.validator;

import com.demo.gradle_demo.anno.Number;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberValidator implements ConstraintValidator<Number,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 检查长度
        if (value.length() != 3 && value.length() != 11) {
            return false;
        }

        // 检查是否全为数字
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

}