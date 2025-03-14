package com.harryberlin.cvnest.util.validator;

import com.harryberlin.cvnest.util.annotation.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.experimental.FieldDefaults;

import java.util.regex.Pattern;

@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailValidator implements ConstraintValidator<Email, String> {

    static String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isEmpty()) {
            return false; //
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }
}
