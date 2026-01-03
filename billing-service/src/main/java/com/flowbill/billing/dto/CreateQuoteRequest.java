package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateQuoteRequest {
    private Long projectId;
    private List<QuoteLineRequest> lines;
    private BigDecimal tvaRate;
    private String notes;
}
