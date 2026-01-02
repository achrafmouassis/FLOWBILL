package com.flowbill.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber; // INV-ABC-2026-001

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "quote_id", nullable = false)
    private Long quoteId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status; // PENDING, PAID, CANCELLED

    @Column(name = "amount_ht", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountHt;

    @Column(name = "tva_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal tvaRate;

    @Column(name = "tva_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal tvaAmount;

    @Column(name = "amount_ttc", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountTtc;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "pdf_generated_at")
    private LocalDateTime pdfGeneratedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private Long cancelledBy;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(length = 3)
    private String currency = "MAD";

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLine> lines = new ArrayList<>();
}
