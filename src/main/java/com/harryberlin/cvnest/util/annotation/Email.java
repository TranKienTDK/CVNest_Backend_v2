package com.harryberlin.cvnest.util.annotation;

import com.harryberlin.cvnest.util.validator.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    String message() default "Email is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
