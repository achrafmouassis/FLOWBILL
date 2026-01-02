package com.flowbill.billing.repository;

import com.flowbill.billing.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByProjectId(Long projectId);

    @Query("SELECT MAX(CAST(SUBSTRING(q.quoteNumber FROM '.*-([0-9]+)$') AS long)) FROM Quote q WHERE q.tenantId = :tenantId AND q.quoteNumber LIKE :yearPattern")
    Long findMaxSequenceForTenant(@Param("tenantId") String tenantId, @Param("yearPattern") String yearPattern);
}
