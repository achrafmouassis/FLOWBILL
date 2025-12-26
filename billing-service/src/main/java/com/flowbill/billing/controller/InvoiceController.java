package com.flowbill.billing.controller;

import com.flowbill.billing.entity.Invoice;
import com.flowbill.billing.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/billing")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${time.service.url}")
    private String timeServiceUrl;

    @PostMapping("/generate")
    public Invoice generateInvoice(@RequestHeader("X-Tenant-ID") String tenantId) {
        // Call Time Service (passing the Tenant ID header for routing)
        // In real app, use FeignRequestInterceptor. Here manually adding header logic to RestTemplate is needed
        // For simplicity of this scaffolding, we assume direct URL call without complex header prop in this block
        // In clean architecture, use a Service Client with Interceptor.
        
        // Mocking the result for "Generate Code" phase
        Invoice invoice = new Invoice();
        invoice.setGeneratedAt(LocalDateTime.now());
        invoice.setAmount(new BigDecimal("1000.00")); // Mock calculation
        invoice.setDetails("Generated based on approved time entries.");
        
        return invoiceRepository.save(invoice);
    }
}
