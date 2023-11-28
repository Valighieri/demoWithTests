package com.example.demowithtests.util.annotations.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BlockedCountriesValidator.class)
public @interface BlockedCountries {
    String message() default "Users from that country are blocked";

    String[] contains() default {"Russian Federation", "RF", "Russia"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
