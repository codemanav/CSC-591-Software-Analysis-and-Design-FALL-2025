package com.ecocycle.transactions.dto;

import com.ecocycle.transactions.model.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionStatusRequest(@NotNull TransactionStatus status) {}
