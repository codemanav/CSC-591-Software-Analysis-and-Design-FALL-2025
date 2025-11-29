package com.ecocycle.transactions.controller;

import com.ecocycle.transactions.dto.*;
import com.ecocycle.transactions.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    // Refactoring: Extract Constant - Removes Magic Number smell (substring(7) for "Bearer " prefix)
    private static final int BEARER_PREFIX_LENGTH = 7; // "Bearer ".length()

    private final TransactionService service;

    @PostMapping("/offer")
    public ResponseEntity<TransactionDto> offer(@Valid @RequestBody CreateOfferRequest req,
                                                HttpServletRequest request) {
        Long buyerId = (Long) request.getAttribute("userId");
        String token = extractTokenFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOffer(req, buyerId, token));
    }


    @PostMapping("/donate")
    public ResponseEntity<TransactionDto> donate(@Valid @RequestBody ClaimDonationRequest req, HttpServletRequest request) {
        Long buyerId = (Long) request.getAttribute("userId");
        String token = extractTokenFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.claimDonation(req, buyerId, token));
    }

    /**
     * Extracts JWT token from Authorization header.
     * Refactoring: Extract Method - Removes Magic Number and duplicate code.
     * 
     * @param request HTTP request containing Authorization header
     * @return JWT token without "Bearer " prefix
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader != null ? authHeader.substring(BEARER_PREFIX_LENGTH) : "";
    }

    @GetMapping("/{id}")
    public TransactionDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public TransactionDto update(@PathVariable Long id, @Valid @RequestBody UpdateTransactionStatusRequest req) {
        return service.updateStatus(id, req);
    }
}
