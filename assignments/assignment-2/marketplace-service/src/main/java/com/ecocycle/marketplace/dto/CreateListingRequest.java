package com.ecocycle.marketplace.dto;

import com.ecocycle.marketplace.model.ListingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateListingRequest(
        @NotBlank String title,
        String description,
        @NotNull ListingType type,
        BigDecimal price,
        String condition,
        String location
) {}
