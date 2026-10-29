package com.ecocycle.transactions.dto;

import jakarta.validation.constraints.NotNull;

public record ClaimDonationRequest(
        @NotNull Long listingId
) {}