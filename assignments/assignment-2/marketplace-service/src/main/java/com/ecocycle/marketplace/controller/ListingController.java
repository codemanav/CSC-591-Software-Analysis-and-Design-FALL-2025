package com.ecocycle.marketplace.controller;

import com.ecocycle.marketplace.dto.CreateListingRequest;
import com.ecocycle.marketplace.dto.ListingDto;
import com.ecocycle.marketplace.model.ListingType;
import com.ecocycle.marketplace.service.ListingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService service;

    @PostMapping
    public ResponseEntity<ListingDto> create(@Valid @RequestBody CreateListingRequest req,
                                             HttpServletRequest request) {
        Long ownerId = (Long) request.getAttribute("userId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, ownerId));
    }

    @GetMapping
    public List<ListingDto> list(@RequestParam Optional<ListingType> type) {
        return service.list(type);
    }

    @GetMapping("/{id}")
    public ListingDto get(@PathVariable Long id) {
        return service.get(id);
    }
}
