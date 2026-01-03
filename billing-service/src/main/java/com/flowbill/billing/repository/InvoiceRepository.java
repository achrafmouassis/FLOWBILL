package com.flowbill.billing.repository;

import com.flowbill.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByProjectId(Long projectId);

    boolean existsByQuoteId(Long quoteId);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(invoice_number FROM '.*-([0-9]+)$') AS bigint)) FROM invoices WHERE tenant_id = :tenantId AND invoice_number LIKE :yearPattern", nativeQuery = true)
    Long findMaxSequenceForTenant(@Param("tenantId") String tenantId, @Param("yearPattern") String yearPattern);
}
