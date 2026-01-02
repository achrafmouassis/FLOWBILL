package com.flowbill.billing.service;

import com.flowbill.billing.entity.Invoice;
import com.flowbill.billing.exception.BillingExceptions.NotFoundException;
import com.flowbill.billing.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfGeneratorService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${billing.pdf.storage-path:/var/flowbill/invoices}")
    private String storagePath;

    /**
     * Génère un PDF de facture
     * 
     * @param invoiceId ID de la facture
     * @return URL du PDF généré
     */
    public String generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new NotFoundException("Facture non trouvée"));

        // Mock Tenant Info (should come from tenant-service)
        Map<String, String> tenant = new HashMap<>();
        tenant.put("name", "Société " + invoice.getTenantId());
        tenant.put("address", "123 Bd Mohamed V");
        tenant.put("city", "Casablanca");
        tenant.put("country", "Maroc");
        tenant.put("ice", "001529384000054");
        tenant.put("phone", "+212 5 22 00 00 00");

        try {
            // Créer contexte pour le template
            Context context = new Context();
            context.setVariable("invoice", invoice);
            context.setVariable("tenant", tenant);
            context.setVariable("lines", invoice.getLines());
            context.setVariable("generatedAt", LocalDateTime.now());

            // Générer HTML depuis template Thymeleaf
            String html = templateEngine.process("invoice-template", context);

            // Convertir HTML en PDF
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            byte[] pdfBytes = outputStream.toByteArray();

            // Sauvegarder le fichier
            String filename = invoice.getInvoiceNumber().replace("/", "-") + ".pdf";
            Path tenantPath = Paths.get(storagePath, invoice.getTenantId());
            Files.createDirectories(tenantPath);
            Path filepath = tenantPath.resolve(filename);

            try (OutputStream os = new FileOutputStream(filepath.toFile())) {
                os.write(pdfBytes);
            }

            // Retourner URL relative
            return "/api/billing/invoices/" + invoice.getId() + "/pdf";

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }
}
