package com.flowbill.billing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "quote_lines")
@Getter
@Setter
public class QuoteLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private Quote quote;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "total_ht", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalHt;

    @Column(name = "task_id")
    private Long taskId;

    @Type(JsonBinaryType.class)
    @Column(name = "time_entry_ids", columnDefinition = "jsonb")
    private List<Long> timeEntryIds;
}
