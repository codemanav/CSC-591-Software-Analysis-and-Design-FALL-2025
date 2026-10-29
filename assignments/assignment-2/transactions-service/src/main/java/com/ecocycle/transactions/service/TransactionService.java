package com.ecocycle.transactions.service;

import com.ecocycle.transactions.client.MarketplaceClient;
import com.ecocycle.transactions.client.ListingDto;
import com.ecocycle.transactions.client.UsersClient;
import com.ecocycle.transactions.dto.*;
import com.ecocycle.transactions.exception.GreenScoreUpdateException;
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

    // Refactoring: Extract Constants - Removes Magic Number smells (5 and 10)
    private static final int BUYER_GREEN_SCORE_INCREMENT = 5;
    private static final int SELLER_GREEN_SCORE_INCREMENT = 10;

    private final TransactionRepository repo;
    private final MarketplaceClient marketplace;
    private final UsersClient users;

    public TransactionDto createOffer(CreateOfferRequest req, Long buyerId, String token) {
        ListingDto listing = marketplace.getListing(req.listingId(), token);
        if (listing == null) throw new RuntimeException("Listing not found");

        validateListingTypeForOffer(listing);

        Transaction tx = createPendingTransaction(req.listingId(), buyerId, listing.ownerId(), req.offerAmount());
        return TransactionDto.from(repo.save(tx));
    }

    /**
     * Validates that the listing type is suitable for offers (SALE or RENTAL).
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param listing The listing to validate
     * @throws RuntimeException if listing type is not SALE or RENTAL
     */
    private void validateListingTypeForOffer(ListingDto listing) {
        if (!("SALE".equals(listing.type()) || "RENTAL".equals(listing.type()))) {
            throw new RuntimeException("Offers only allowed on SALE or RENTAL listings");
        }
    }

    /**
     * Creates a new pending transaction.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param listingId The listing ID
     * @param buyerId The buyer ID
     * @param sellerId The seller ID
     * @param offerAmount The offer amount
     * @return A new Transaction with PENDING status
     */
    private Transaction createPendingTransaction(Long listingId, Long buyerId, Long sellerId, BigDecimal offerAmount) {
        Instant now = Instant.now();
        return new Transaction(
                null,
                listingId,
                buyerId,
                sellerId,
                TransactionStatus.PENDING,
                offerAmount,
                now,
                now
        );
    }


    public TransactionDto claimDonation(ClaimDonationRequest req, Long receiverId, String token) {
        ListingDto listing = marketplace.getListing(req.listingId(), token);
        if (listing == null) {
            throw new RuntimeException("Listing not found");
        }
        
        validateListingTypeForDonation(listing);

        Transaction tx = createConfirmedDonationTransaction(req.listingId(), receiverId, listing.ownerId());
        return TransactionDto.from(repo.save(tx));
    }

    /**
     * Validates that the listing type is DONATION.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param listing The listing to validate
     * @throws RuntimeException if listing type is not DONATION
     */
    private void validateListingTypeForDonation(ListingDto listing) {
        if (!"DONATION".equals(listing.type())) {
            throw new RuntimeException("This listing is not available for donation");
        }
    }

    /**
     * Creates a new confirmed donation transaction with zero price.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param listingId The listing ID
     * @param receiverId The receiver ID
     * @param ownerId The owner ID
     * @return A new Transaction with CONFIRMED status and zero price
     */
    private Transaction createConfirmedDonationTransaction(Long listingId, Long receiverId, Long ownerId) {
        Instant now = Instant.now();
        return new Transaction(
                null,
                listingId,
                receiverId,
                ownerId,
                TransactionStatus.CONFIRMED,
                BigDecimal.ZERO,
                now,
                now
        );
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
            updateGreenScoresForCompletedTransaction(tx);
        }
        
        repo.save(tx);
        return TransactionDto.from(tx);
    }

    /**
     * Updates green scores for buyer and seller when a transaction is completed.
     * Refactoring: Extract Method - Reduces Long Method smell and Feature Envy.
     * 
     * @param tx The completed transaction
     * @throws GreenScoreUpdateException if score update fails
     */
    private void updateGreenScoresForCompletedTransaction(Transaction tx) {
        try {
            users.incrementGreenScore(tx.getBuyerId(), BUYER_GREEN_SCORE_INCREMENT);
            users.incrementGreenScore(tx.getSellerId(), SELLER_GREEN_SCORE_INCREMENT);
        } catch (Exception e) {
            throw new GreenScoreUpdateException("Failed to update user scores", e);
        }
    }
}
