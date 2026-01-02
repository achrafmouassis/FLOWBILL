package com.flowbill.billing.repository;

import com.flowbill.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByProjectId(Long projectId);

    boolean existsByQuoteId(Long quoteId);

    @Query("SELECT MAX(CAST(SUBSTRING(i.invoiceNumber FROM '.*-([0-9]+)$') AS long)) FROM Invoice i WHERE i.tenantId = :tenantId AND i.invoiceNumber LIKE :yearPattern")
    Long findMaxSequenceForTenant(@Param("tenantId") String tenantId, @Param("yearPattern") String yearPattern);
}
