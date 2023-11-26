package com.f4pl0.pinnacle.portfolioservice.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PastOrPresentTimestampValidator implements ConstraintValidator<PastOrPresentTimestamp, Long> {
    @Override
    public boolean isValid(Long timestamp, ConstraintValidatorContext context) {
        if (timestamp == null) {
            return true;
        }
        long currentTimestamp = System.currentTimeMillis();
        return timestamp <= currentTimestamp;
    }
}