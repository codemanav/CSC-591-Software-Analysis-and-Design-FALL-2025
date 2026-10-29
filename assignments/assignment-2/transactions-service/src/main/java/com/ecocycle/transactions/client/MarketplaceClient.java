package com.ecocycle.transactions.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MarketplaceClient {

    // Refactoring: Extract Constant - Removes Magic String smell
    private static final String BEARER_PREFIX = "Bearer ";

    private final WebClient webClient;
    private final String baseUrl;

    public MarketplaceClient(@Value("${marketplace.base-url:http://localhost:8081}") String baseUrl) {
        this.webClient = WebClient.builder().build();
        this.baseUrl = baseUrl;
    }

    public ListingDto getListing(Long id, String token) {
        return executeGetRequest(buildListingUri(id), buildAuthorizationHeader(token));
    }

    /**
     * Executes a GET request using WebClient.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param uri The URI to request
     * @param authHeader The Authorization header value
     * @return The response body as ListingDto
     */
    private ListingDto executeGetRequest(String uri, String authHeader) {
        return webClient.get()
                .uri(uri)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(ListingDto.class)
                .block();
    }

    /**
     * Builds the URI template for getting a listing.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param id The listing ID
     * @return The URI template string
     */
    private String buildListingUri(Long id) {
        return baseUrl + "/listings/{id}";
    }

    /**
     * Builds the Authorization header with Bearer token.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param token The JWT token
     * @return The Authorization header value
     */
    private String buildAuthorizationHeader(String token) {
        return BEARER_PREFIX + token;
    }
}
