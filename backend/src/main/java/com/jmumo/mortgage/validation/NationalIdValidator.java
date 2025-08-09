package com.jmumo.mortgage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@jakarta.validation.Constraint(validatedBy = NationalIdValidator.NationalIdValidatorImpl.class)
public @interface NationalIdValidator {

    String message() default "Invalid National ID format";

    Class<?>[] groups() default {};

    Class<? extends jakarta.validation.Payload>[] payload() default {};

    class NationalIdValidatorImpl implements ConstraintValidator<NationalIdValidator, String> {

        private static final Pattern NATIONAL_ID_PATTERN = Pattern.compile("^[0-9]{8,12}$");

        @Override
        public void initialize(NationalIdValidator constraintAnnotation) {
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return false;
            }

            if (!NATIONAL_ID_PATTERN.matcher(value).matches()) {
                return false;
            }

            // Additional validation logic can be added here
            return true;
        }

    }
}