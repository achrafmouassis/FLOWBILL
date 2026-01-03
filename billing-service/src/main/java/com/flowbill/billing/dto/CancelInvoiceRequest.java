package com.flowbill.billing.dto;

import lombok.Data;

@Data
public class CancelInvoiceRequest {
    private String reason;
}
