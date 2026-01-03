package com.flowbill.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
