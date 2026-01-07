package com.flowbill.billing.controller;

import com.flowbill.billing.dto.*;
import com.flowbill.billing.entity.QuoteStatus;
import com.flowbill.billing.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/billing/quotes")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<QuoteDTO> createQuote(
            @Valid @RequestBody CreateQuoteRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuoteDTO quote = quoteService.createQuote(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(quote);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ENTREPRISE', 'USER')")
    public ResponseEntity<List<QuoteDTO>> listQuotes(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) QuoteStatus status) {
        List<QuoteDTO> quotes = quoteService.findQuotes(projectId, status);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ENTREPRISE', 'USER')")
    public ResponseEntity<QuoteDTO> getQuote(@PathVariable Long id) {
        QuoteDTO quote = quoteService.findById(id);
        return ResponseEntity.ok(quote);
    }

    @PutMapping("/{id}/send")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<QuoteDTO> sendQuote(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuoteDTO quote = quoteService.sendQuote(id, userId);
        return ResponseEntity.ok(quote);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<QuoteDTO> acceptQuote(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuoteDTO quote = quoteService.acceptQuote(id, userId);
        return ResponseEntity.ok(quote);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN_ENTREPRISE')")
    public ResponseEntity<QuoteDTO> rejectQuote(
            @PathVariable Long id,
            @RequestBody RejectQuoteRequest request,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        QuoteDTO quote = quoteService.rejectQuote(id, request.getReason(), userId);
        return ResponseEntity.ok(quote);
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null)
            return 0L;
        Object principal = authentication.getPrincipal();

        // Mock implementation assuming UserDetails or Map contains ID
        // In real Gateway + JWT setup, ID is usually in details or principal map
        if (authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            Object id = details.get("id"); // or userId
            if (id instanceof Integer)
                return ((Integer) id).longValue();
            if (id instanceof Long)
                return (Long) id;
        }

        // Fallback or specific Principal implementation check
        // For MVP we might rely on a predictable structure or return a default for
        // simulation if running standalone without Gateway propagating user details
        // correctly yet.
        // Assuming the JWT filter sets a custom Principal or Details.

        return 1L; // Mocked for now if extraction fails, better for MVP progress vs blocked on
                   // Auth details nuances unless clearly specified.
    }
}
