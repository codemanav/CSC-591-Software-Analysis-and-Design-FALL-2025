package com.ecocycle.transactions.dto;

import com.ecocycle.transactions.model.Transaction;
import com.ecocycle.transactions.model.TransactionStatus;

import java.math.BigDecimal;

public record TransactionDto(Long id, Long listingId, Long buyerId, Long sellerId,
                             TransactionStatus status, BigDecimal agreedPrice) {
    public static TransactionDto from(Transaction t) {
        return new TransactionDto(t.getId(), t.getListingId(), t.getBuyerId(),
                t.getSellerId(), t.getStatus(), t.getAgreedPrice());
    }
}
