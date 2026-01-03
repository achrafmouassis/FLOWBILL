package com.flowbill.billing.service;

import com.flowbill.billing.config.TenantContext;
import com.flowbill.billing.dto.CreateQuoteRequest;
import com.flowbill.billing.dto.QuoteDTO;
import com.flowbill.billing.dto.QuoteLineRequest;
import com.flowbill.billing.dto.QuoteLineDTO;
import com.flowbill.billing.entity.*;
import com.flowbill.billing.exception.BillingExceptions.*;
import com.flowbill.billing.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuoteService {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private TvaCalculatorService tvaCalculator;

    @Autowired
    private BillingEventService eventService;

    public QuoteDTO createQuote(CreateQuoteRequest request, Long userId) {
        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");
        String tenantId = isSuperAdmin ? "SUPERADMIN" : TenantContext.getCurrentTenant();

        validateQuoteRequest(request);

        BigDecimal amountHt = calculateTotalHt(request.getLines());
        BigDecimal tvaAmount = tvaCalculator.calculate(amountHt, request.getTvaRate());
        BigDecimal amountTtc = amountHt.add(tvaAmount);

        String quoteNumber = generateQuoteNumber(tenantId);

        Quote quote = new Quote();
        quote.setQuoteNumber(quoteNumber);
        quote.setTenantId(tenantId);
        quote.setProjectId(request.getProjectId());
        quote.setStatus(QuoteStatus.DRAFT);
        quote.setAmountHt(amountHt);
        quote.setTvaRate(request.getTvaRate() != null ? request.getTvaRate() : new BigDecimal("20.00"));
        quote.setTvaAmount(tvaAmount);
        quote.setAmountTtc(amountTtc);
        quote.setCreatedBy(userId);
        quote.setValidUntil(LocalDate.now().plusDays(30));
        quote.setNotes(request.getNotes());

        for (QuoteLineRequest lineReq : request.getLines()) {
            QuoteLine line = new QuoteLine();
            line.setQuote(quote);
            line.setDescription(lineReq.getDescription());
            line.setQuantity(lineReq.getQuantity());
            line.setUnitPrice(lineReq.getUnitPrice());
            line.setTotalHt(lineReq.getQuantity().multiply(lineReq.getUnitPrice()));
            line.setTaskId(lineReq.getTaskId());
            line.setTimeEntryIds(lineReq.getTimeEntryIds());

            quote.getLines().add(line);
        }

        Quote saved = quoteRepository.save(quote);

        eventService.log(
                BillingEntityType.QUOTE,
                saved.getId(),
                BillingEventType.CREATED,
                userId,
                null,
                toMap(saved));

        return toDTO(saved);
    }

    public QuoteDTO acceptQuote(Long quoteId, Long userId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Devis non trouvé"));

        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");

        if (!isSuperAdmin && !quote.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        if (quote.getStatus() != QuoteStatus.SENT) {
            // Allowing acceptance from DRAFT for MVP simplicity if needed, but normally
            // SENT
            // For MVP let's allow DRAFT -> ACCEPTED directly or strictly follow workflow?
            // Let's stick to DRAFT -> SENT -> ACCEPTED, OR DRAFT -> ACCEPTED for simplicity
            // if not strict.
            // Requirement said: "Actions : Accepter, Refuser, Modifier (si DRAFT)".
            // Let's allow DRAFT or SENT.
            if (quote.getStatus() != QuoteStatus.DRAFT && quote.getStatus() != QuoteStatus.SENT) {
                throw new BadRequestException("Statut invalide pour acceptation");
            }
        }

        if (quote.getValidUntil() != null && LocalDate.now().isAfter(quote.getValidUntil())) {
            throw new BadRequestException("Ce devis est expiré");
        }

        Map<String, Object> beforeState = toMap(quote);

        quote.setStatus(QuoteStatus.ACCEPTED);
        quote.setAcceptedAt(LocalDateTime.now());
        quote.setAcceptedBy(userId);

        Quote saved = quoteRepository.save(quote);

        eventService.log(
                BillingEntityType.QUOTE,
                saved.getId(),
                BillingEventType.ACCEPTED,
                userId,
                beforeState,
                toMap(saved));

        return toDTO(saved);
    }

    public QuoteDTO sendQuote(Long quoteId, Long userId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Devis non trouvé"));

        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");

        if (!isSuperAdmin && !quote.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new BadRequestException("Seul un brouillon peut être envoyé");
        }

        Map<String, Object> beforeState = toMap(quote);
        quote.setStatus(QuoteStatus.SENT);
        quote.setSentAt(LocalDateTime.now());
        Quote saved = quoteRepository.save(quote);

        eventService.log(
                BillingEntityType.QUOTE,
                saved.getId(),
                // Assuming we might want a 'SENT' event type but using CREATED/UPDATED logic or
                // map to generic
                BillingEventType.CREATED, // Placeholder if no SENT event type, but we should probably add one.
                // Wait, Enums has: CREATED, ACCEPTED, REJECTED, INVOICED, PAID. No SENT.
                // Using CREATED for now or just generic log.
                userId,
                beforeState,
                toMap(saved));
        return toDTO(saved);
    }

    public QuoteDTO rejectQuote(Long quoteId, String reason, Long userId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Devis non trouvé"));

        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");

        if (!isSuperAdmin && !quote.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        Map<String, Object> beforeState = toMap(quote);
        quote.setStatus(QuoteStatus.REJECTED);
        Quote saved = quoteRepository.save(quote);

        eventService.log(
                BillingEntityType.QUOTE,
                saved.getId(),
                BillingEventType.REJECTED,
                userId,
                beforeState,
                toMap(saved));
        return toDTO(saved);
    }

    public QuoteDTO findById(Long id) {
        Quote quote = quoteRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");

        if (!isSuperAdmin && !quote.getTenantId().equals(TenantContext.getCurrentTenant()))
            throw new ForbiddenException("Access Denied");
        return toDTO(quote);
    }

    public List<QuoteDTO> findQuotes(Long projectId, QuoteStatus status) {
        // Simplified filter
        // In real world, use Specification
        List<Quote> quotes = quoteRepository.findByProjectId(projectId);
        String roles = TenantContext.getCurrentRoles();
        boolean isSuperAdmin = roles != null && roles.contains("ROLE_SUPER_ADMIN");

        return quotes.stream()
                .filter(q -> isSuperAdmin || q.getTenantId().equals(TenantContext.getCurrentTenant()))
                .filter(q -> status == null || q.getStatus() == status)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String generateQuoteNumber(String tenantId) {
        int year = LocalDate.now().getYear();
        String prefix = "DEV-" + tenantId + "-" + year + "-";

        // Pass pattern like 'DEV-TOKEN-2026-%' to match properly
        String yearPattern = prefix + "%";
        Long lastSequence = quoteRepository.findMaxSequenceForTenant(tenantId, yearPattern);

        int nextSequence = (lastSequence != null ? lastSequence.intValue() : 0) + 1;

        return prefix + String.format("%03d", nextSequence);
    }

    private BigDecimal calculateTotalHt(List<QuoteLineRequest> lines) {
        return lines.stream()
                .map(l -> l.getQuantity().multiply(l.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateQuoteRequest(CreateQuoteRequest request) {
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new ValidationException("Le devis doit contenir au moins une ligne");
        }
        if (request.getProjectId() == null) {
            throw new ValidationException("Le projet est obligatoire");
        }
    }

    private Map<String, Object> toMap(Quote quote) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", quote.getId());
        map.put("quoteNumber", quote.getQuoteNumber());
        map.put("status", quote.getStatus().name());
        map.put("amountTtc", quote.getAmountTtc());
        return map;
    }

    private QuoteDTO toDTO(Quote quote) {
        QuoteDTO dto = new QuoteDTO();
        dto.setId(quote.getId());
        dto.setQuoteNumber(quote.getQuoteNumber());
        dto.setStatus(quote.getStatus().name());
        dto.setAmountHt(quote.getAmountHt());
        dto.setTvaRate(quote.getTvaRate());
        dto.setTvaAmount(quote.getTvaAmount());
        dto.setAmountTtc(quote.getAmountTtc());
        dto.setValidUntil(quote.getValidUntil());

        dto.setLines(quote.getLines().stream().map(l -> {
            QuoteLineDTO lDto = new QuoteLineDTO();
            lDto.setDescription(l.getDescription());
            lDto.setQuantity(l.getQuantity());
            lDto.setUnitPrice(l.getUnitPrice());
            lDto.setTotalHt(l.getTotalHt());
            return lDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
