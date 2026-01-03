package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class QuoteLineRequest {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private Long taskId;
    private List<Long> timeEntryIds;
}
