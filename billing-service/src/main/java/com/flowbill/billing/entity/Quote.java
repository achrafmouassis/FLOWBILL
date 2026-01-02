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
@Table(name = "quotes")
@Getter
@Setter
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_number", unique = true, nullable = false)
    private String quoteNumber; // DEV-ABC-2026-001

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status; // DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED

    @Column(name = "amount_ht", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountHt;

    @Column(name = "tva_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal tvaRate = new BigDecimal("20.00");

    @Column(name = "tva_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal tvaAmount;

    @Column(name = "amount_ttc", precision = 12, scale = 2, nullable = false)
    private BigDecimal amountTtc;

    @Column(length = 3)
    private String currency = "MAD";

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "accepted_by")
    private Long acceptedBy;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteLine> lines = new ArrayList<>();
}
