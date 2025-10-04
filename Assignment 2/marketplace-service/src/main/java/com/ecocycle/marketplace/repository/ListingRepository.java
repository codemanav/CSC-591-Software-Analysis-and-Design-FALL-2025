package com.ecocycle.marketplace.repository;

import com.ecocycle.marketplace.model.Listing;
import com.ecocycle.marketplace.model.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByType(ListingType type);
}
