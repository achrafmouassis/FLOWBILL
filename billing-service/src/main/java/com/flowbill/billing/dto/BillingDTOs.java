package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateQuoteRequest {
    private Long projectId;
    private List<QuoteLineRequest> lines;
    private BigDecimal tvaRate;
    private String notes;
}

@Data
public class QuoteLineRequest {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private Long taskId;
    private List<Long> timeEntryIds;
}

@Data
public class QuoteDTO {
    private Long id;
    private String quoteNumber;
    private String status;
    private BigDecimal amountHt;
    private BigDecimal tvaRate;
    private BigDecimal tvaAmount;
    private BigDecimal amountTtc;
    private LocalDate validUntil;
    private List<QuoteLineDTO> lines;
}

@Data
public class QuoteLineDTO {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalHt;
}

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

@Data
public class InvoiceLineDTO {
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalHt;
}

@Data
public class RejectQuoteRequest {
    private String reason;
}

@Data
public class MarkPaidRequest {
    private BigDecimal paidAmount;
}

@Data
public class CancelInvoiceRequest {
    private String reason;
}
