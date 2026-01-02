package com.flowbill.billing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TvaCalculatorService {

    @Value("${billing.tva.default-rate:20.00}")
    private BigDecimal defaultTvaRate;

    public BigDecimal calculate(BigDecimal amountHt, BigDecimal rate) {
        if (rate == null) {
            rate = defaultTvaRate;
        }
        return amountHt.multiply(rate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
    }
}
