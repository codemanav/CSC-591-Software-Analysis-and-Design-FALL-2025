package com.ecocycle.transactions.service;

import com.ecocycle.transactions.client.MarketplaceClient;
import com.ecocycle.transactions.client.ListingDto;
import com.ecocycle.transactions.client.UsersClient;
import com.ecocycle.transactions.dto.*;
import com.ecocycle.transactions.model.Transaction;
import com.ecocycle.transactions.model.TransactionStatus;
import com.ecocycle.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repo;
    private final MarketplaceClient marketplace;
    private final UsersClient users;

    public TransactionDto createOffer(CreateOfferRequest req, Long buyerId, String token) {
        ListingDto listing = marketplace.getListing(req.listingId(), token);
        if (listing == null) throw new RuntimeException("Listing not found");

        if (!("SALE".equals(listing.type()) || "RENTAL".equals(listing.type()))) {
            throw new RuntimeException("Offers only allowed on SALE or RENTAL listings");
        }

        Transaction tx = new Transaction(
                null,
                req.listingId(),
                buyerId,
                listing.ownerId(),
                TransactionStatus.PENDING,
                req.offerAmount(),
                Instant.now(),
                Instant.now()
        );
        return TransactionDto.from(repo.save(tx));
    }


    public TransactionDto claimDonation(ClaimDonationRequest req, Long receiverId, String token) {
        ListingDto listing = marketplace.getListing(req.listingId(), token);
        if (listing == null) {
            throw new RuntimeException("Listing not found");
        }
        if (!"DONATION".equals(listing.type())) {
            throw new RuntimeException("This listing is not available for donation");
        }

        Transaction tx = new Transaction(
                null,
                req.listingId(),
                receiverId,
                listing.ownerId(),
                TransactionStatus.CONFIRMED,
                BigDecimal.ZERO,
                Instant.now(),
                Instant.now()
        );
        return TransactionDto.from(repo.save(tx));
    }

    public TransactionDto get(Long id) {
        return repo.findById(id)
                .map(TransactionDto::from)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public TransactionDto updateStatus(Long id, UpdateTransactionStatusRequest req) {
        Transaction tx = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        tx.setStatus(req.status());
        tx.setUpdatedAt(Instant.now());
        if (tx.getStatus() == TransactionStatus.COMPLETED) {
            try {
                users.incrementGreenScore(tx.getBuyerId(), 5);
                users.incrementGreenScore(tx.getSellerId(), 10);
            } catch (Exception e) {
                throw new RuntimeException("Failed to update user scores: " + e.getMessage(), e);
            }
        }
        repo.save(tx);
        return TransactionDto.from(tx);
    }
}
