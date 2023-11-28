package com.example.demowithtests.util.annotations.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class BlockedCountriesValidator implements ConstraintValidator<BlockedCountries, String> {

    private String[] countries;

    @Override
    public void initialize(BlockedCountries constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        countries = constraintAnnotation.contains();
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(countries).noneMatch(country::equals);
    }
}
