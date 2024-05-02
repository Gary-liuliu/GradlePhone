package com.demo.gradle_demo.validator;

import com.demo.gradle_demo.anno.Number;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberValidator implements ConstraintValidator<Number,String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() == 3 || value.length() == 11;
    }
}