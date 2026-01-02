package com.flowbill.billing.controller;

import com.flowbill.billing.dto.*;
import com.flowbill.billing.entity.InvoiceStatus;
import com.flowbill.billing.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/from-quote/{quoteId}")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<InvoiceDTO> createInvoiceFromQuote(
            @PathVariable Long quoteId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        InvoiceDTO invoice = invoiceService.createInvoiceFromQuote(quoteId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ENTREPRISE', 'USER')")
    public ResponseEntity<List<InvoiceDTO>> listInvoices(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) InvoiceStatus status) {
        List<InvoiceDTO> invoices = invoiceService.findInvoices(projectId, status);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ENTREPRISE', 'USER')")
    public ResponseEntity<InvoiceDTO> getInvoice(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.findById(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN_ENTREPRISE', 'USER')")
    public ResponseEntity<Resource> downloadInvoicePdf(@PathVariable Long id) {
        byte[] pdfBytes = invoiceService.getInvoicePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ByteArrayResource(pdfBytes));
    }

    @PutMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<InvoiceDTO> markAsPaid(
            @PathVariable Long id,
            @Valid @RequestBody MarkPaidRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        InvoiceDTO invoice = invoiceService.markAsPaid(id, request.getPaidAmount(), userId);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<InvoiceDTO> cancelInvoice(
            @PathVariable Long id,
            @RequestBody CancelInvoiceRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        InvoiceDTO invoice = invoiceService.cancelInvoice(id, request.getReason(), userId);
        return ResponseEntity.ok(invoice);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null)
            return 0L;
        // Mock extraction - see QuoteController for details
        return 1L;
    }
}
