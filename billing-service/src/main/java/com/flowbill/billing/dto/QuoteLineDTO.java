package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuoteLineDTO {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalHt;
}
