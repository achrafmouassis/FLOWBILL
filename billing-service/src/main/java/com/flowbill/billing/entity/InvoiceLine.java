package com.flowbill.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_lines")
@Getter
@Setter
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_ht", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalHt;
}
