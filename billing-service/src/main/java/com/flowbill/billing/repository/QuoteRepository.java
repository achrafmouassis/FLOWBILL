package com.flowbill.billing.repository;

import com.flowbill.billing.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByProjectId(Long projectId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(quote_number FROM '.*-([0-9]+)$') AS bigint)) FROM quotes WHERE tenant_id = :tenantId AND quote_number LIKE :yearPattern", nativeQuery = true)
    Long findMaxSequenceForTenant(@Param("tenantId") String tenantId, @Param("yearPattern") String yearPattern);
}
