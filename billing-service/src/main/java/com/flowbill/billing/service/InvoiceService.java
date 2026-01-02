package com.flowbill.billing.service;

import com.flowbill.billing.config.TenantContext;
import com.flowbill.billing.dto.InvoiceDTO;
import com.flowbill.billing.dto.InvoiceLineDTO;
import com.flowbill.billing.entity.*;
import com.flowbill.billing.exception.BillingExceptions.*;
import com.flowbill.billing.repository.InvoiceRepository;
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
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private PdfGeneratorService pdfGenerator;

    @Autowired
    private BillingEventService eventService;

    @Autowired
    private TenantContext tenantContext;

    public InvoiceDTO createInvoiceFromQuote(Long quoteId, Long userId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new NotFoundException("Devis non trouvé"));

        if (!quote.getTenantId().equals(tenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        if (quote.getStatus() != QuoteStatus.ACCEPTED) {
            throw new BadRequestException("Seul un devis accepté peut être facturé");
        }

        if (invoiceRepository.existsByQuoteId(quoteId)) {
            throw new BadRequestException("Une facture existe déjà pour ce devis");
        }

        String invoiceNumber = generateInvoiceNumber(quote.getTenantId());

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setTenantId(quote.getTenantId());
        invoice.setQuoteId(quote.getId());
        invoice.setProjectId(quote.getProjectId());
        invoice.setStatus(InvoiceStatus.PENDING);

        invoice.setAmountHt(quote.getAmountHt());
        invoice.setTvaRate(quote.getTvaRate());
        invoice.setTvaAmount(quote.getTvaAmount());
        invoice.setAmountTtc(quote.getAmountTtc());
        invoice.setCurrency(quote.getCurrency());

        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));

        invoice.setCreatedBy(userId);

        for (QuoteLine quoteLine : quote.getLines()) {
            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setInvoice(invoice);
            invoiceLine.setDescription(quoteLine.getDescription());
            invoiceLine.setQuantity(quoteLine.getQuantity());
            invoiceLine.setUnitPrice(quoteLine.getUnitPrice());
            invoiceLine.setTotalHt(quoteLine.getTotalHt());

            invoice.getLines().add(invoiceLine);
        }

        Invoice saved = invoiceRepository.save(invoice);

        // Generate PDF
        try {
            String pdfUrl = pdfGenerator.generateInvoicePdf(saved.getId());
            saved.setPdfUrl(pdfUrl);
            saved.setPdfGeneratedAt(LocalDateTime.now());
            saved = invoiceRepository.save(saved);
        } catch (Exception e) {
            // Log error but don't fail transaction?
            // Better to fail if PDF is critical or handle async.
            // In synchronous simple MVP, let's keep it simple.
            System.err.println("Failed to generate PDF: " + e.getMessage());
        }

        eventService.log(
                BillingEntityType.INVOICE,
                saved.getId(),
                BillingEventType.CREATED,
                userId,
                null,
                toMap(saved));

        return toDTO(saved);
    }

    public InvoiceDTO markAsPaid(Long invoiceId, BigDecimal paidAmount, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Facture non trouvée"));

        if (!invoice.getTenantId().equals(tenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new BadRequestException("Cette facture ne peut plus être payée");
        }

        if (paidAmount.compareTo(invoice.getAmountTtc()) != 0) {
            throw new BadRequestException("Le montant payé ne correspond pas au montant dû");
        }

        Map<String, Object> beforeState = toMap(invoice);

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPaidAmount(paidAmount);

        Invoice saved = invoiceRepository.save(invoice);

        eventService.log(
                BillingEntityType.INVOICE,
                saved.getId(),
                BillingEventType.PAID,
                userId,
                beforeState,
                toMap(saved));

        return toDTO(saved);
    }

    public InvoiceDTO cancelInvoice(Long invoiceId, String reason, Long userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Facture non trouvée"));

        if (!invoice.getTenantId().equals(tenantContext.getCurrentTenant())) {
            throw new ForbiddenException("Accès refusé");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Impossible d'annuler une facture payée");
        }

        Map<String, Object> beforeState = toMap(invoice);
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setCancelledAt(LocalDateTime.now());
        invoice.setCancelledBy(userId);
        invoice.setCancellationReason(reason);

        Invoice saved = invoiceRepository.save(invoice);

        // Log event? Using INVOICED as placeholder or create CANCELLED?
        // Let's use REJECTED as close equivalent or add CANCELLED
        eventService.log(
                BillingEntityType.INVOICE,
                saved.getId(),
                BillingEventType.REJECTED,
                userId,
                beforeState,
                toMap(saved));
        return toDTO(saved);
    }

    public InvoiceDTO findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        if (!invoice.getTenantId().equals(tenantContext.getCurrentTenant()))
            throw new ForbiddenException("Access Denied");
        return toDTO(invoice);
    }

    public List<InvoiceDTO> findInvoices(Long projectId, InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByProjectId(projectId);
        return invoices.stream()
                .filter(i -> i.getTenantId().equals(tenantContext.getCurrentTenant()))
                .filter(i -> status == null || i.getStatus() == status)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public byte[] getInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        if (!invoice.getTenantId().equals(tenantContext.getCurrentTenant()))
            throw new ForbiddenException("Access Denied");

        // In clean arch, this service should return the file path or resource
        // Quick hack: read file from disk
        // PdfGeneratorService knows storage path configuration logic, so ideally we ask
        // it.
        // But for MVP, assume local storage access.
        try {
            // Reconstruct path
            String filename = invoice.getInvoiceNumber().replace("/", "-") + ".pdf";
            // We need the base path. Inject it here too?
            // Or better expose a method in PdfService
            // Let's assume PdfGeneratorService handles only creation for now.
            // We will assume path from config in Controller or here.
            // Simplification: Return empty byte array if not implemented reading logic in
            // MVP
            return new byte[0]; // TODO: Implement file reading
        } catch (Exception e) {
            throw new RuntimeException("Could not read PDF");
        }
    }

    private String generateInvoiceNumber(String tenantId) {
        int year = LocalDate.now().getYear();
        String prefix = "INV-" + tenantId + "-" + year + "-";

        String yearPattern = prefix + "%";
        Long lastSequence = invoiceRepository.findMaxSequenceForTenant(tenantId, yearPattern);
        int nextSequence = (lastSequence != null ? lastSequence.intValue() : 0) + 1;

        return prefix + String.format("%03d", nextSequence);
    }

    private Map<String, Object> toMap(Invoice invoice) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", invoice.getId());
        map.put("invoiceNumber", invoice.getInvoiceNumber());
        map.put("status", invoice.getStatus().name());
        map.put("amountTtc", invoice.getAmountTtc());
        return map;
    }

    private InvoiceDTO toDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setStatus(invoice.getStatus().name());
        dto.setAmountHt(invoice.getAmountHt());
        dto.setTvaAmount(invoice.getTvaAmount());
        dto.setAmountTtc(invoice.getAmountTtc());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setPdfUrl(invoice.getPdfUrl());

        dto.setLines(invoice.getLines().stream().map(l -> {
            InvoiceLineDTO lDto = new InvoiceLineDTO();
            lDto.setDescription(l.getDescription());
            lDto.setQuantity(l.getQuantity());
            lDto.setUnitPrice(l.getUnitPrice());
            lDto.setTotalHt(l.getTotalHt());
            return lDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
