package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String status;
    private BigDecimal amountHt;
    private BigDecimal tvaAmount;
    private BigDecimal amountTtc;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String pdfUrl;
    private List<InvoiceLineDTO> lines;
}
