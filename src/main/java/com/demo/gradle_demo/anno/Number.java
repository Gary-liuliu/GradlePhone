package com.demo.gradle_demo.anno;

import com.demo.gradle_demo.validator.NumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {NumberValidator.class}
)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Number {
    String message() default "查询只能是3位或者11位的数字";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}