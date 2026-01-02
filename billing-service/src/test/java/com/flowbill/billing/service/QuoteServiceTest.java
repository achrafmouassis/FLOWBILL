package com.flowbill.billing.service;

import com.flowbill.billing.config.TenantContext;
import com.flowbill.billing.dto.CreateQuoteRequest;
import com.flowbill.billing.dto.QuoteDTO;
import com.flowbill.billing.dto.QuoteLineRequest;
import com.flowbill.billing.entity.Quote;
import com.flowbill.billing.entity.QuoteStatus;
import com.flowbill.billing.repository.BillingEventRepository;
import com.flowbill.billing.repository.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private TvaCalculatorService tvaCalculator;

    @Mock
    private BillingEventService eventService;

    @Mock
    private TenantContext tenantContext;

    @InjectMocks
    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createQuote_ShouldCalculateTotalsAndSave() {
        // Arrange
        Long userId = 1L;
        String tenantId = "tenant_abc";

        CreateQuoteRequest request = new CreateQuoteRequest();
        request.setProjectId(10L);
        request.setTvaRate(new BigDecimal("20.00"));

        List<QuoteLineRequest> lines = new ArrayList<>();
        QuoteLineRequest line = new QuoteLineRequest();
        line.setDescription("Dev");
        line.setQuantity(new BigDecimal("10"));
        line.setUnitPrice(new BigDecimal("500"));
        lines.add(line);
        request.setLines(lines);

        when(tenantContext.getCurrentTenant()).thenReturn(tenantId);
        when(tvaCalculator.calculate(any(), any())).thenReturn(new BigDecimal("1000.00")); // 20% of 5000
        when(quoteRepository.findMaxSequenceForTenant(any(), any())).thenReturn(null);
        when(quoteRepository.save(any(Quote.class))).thenAnswer(i -> {
            Quote q = i.getArgument(0);
            q.setId(1L);
            return q;
        });

        // Act
        QuoteDTO result = quoteService.createQuote(request, userId);

        // Assert
        assertNotNull(result);
        assertEquals("DEV-tenant_abc-2026-001", result.getQuoteNumber().substring(0, 21)); // Year dynamic
        assertEquals(QuoteStatus.DRAFT.name(), result.getStatus());
        assertEquals(new BigDecimal("5000.00"), result.getAmountHt());
        assertEquals(new BigDecimal("1000.00"), result.getTvaAmount());
        assertEquals(new BigDecimal("6000.00"), result.getAmountTtc());

        verify(eventService).log(any(), any(), any(), any(), any(), any());
    }
}
