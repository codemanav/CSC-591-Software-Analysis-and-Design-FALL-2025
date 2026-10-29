package com.ecocycle.marketplace.service;

import com.ecocycle.marketplace.dto.CreateListingRequest;
import com.ecocycle.marketplace.dto.ListingDto;
import com.ecocycle.marketplace.model.Listing;
import com.ecocycle.marketplace.model.ListingType;
import com.ecocycle.marketplace.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository repo;

    public ListingDto create(CreateListingRequest req, Long ownerId) {
        Listing l = new Listing(
                null,
                req.title(),
                req.description(),
                req.type(),
                req.price(),
                req.condition(),
                req.location(),
                ownerId,
                Instant.now()
        );
        return ListingDto.from(repo.save(l));
    }


    public List<ListingDto> list(Optional<ListingType> type) {
        return (type.isPresent()
                ? repo.findByType(type.get())
                : repo.findAll())
                .stream().map(ListingDto::from).toList();
    }

    public ListingDto get(Long id) {
        return repo.findById(id)
                .map(ListingDto::from)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }
}
