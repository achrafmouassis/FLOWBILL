package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MarkPaidRequest {
    private BigDecimal paidAmount;
}
