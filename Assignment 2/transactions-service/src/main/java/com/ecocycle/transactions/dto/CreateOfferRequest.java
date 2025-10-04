package com.ecocycle.transactions.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOfferRequest(
        @NotNull Long listingId,
        @NotNull BigDecimal offerAmount
) {}
