package com.ecocycle.transactions.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsersClient {

    private final WebClient webClient;
    private final String baseUrl;

    public UsersClient(@Value("${users.base-url:http://localhost:8083}") String baseUrl) {
        this.webClient = WebClient.builder().build();
        this.baseUrl = baseUrl;
    }

    public void incrementGreenScore(Long userId, int delta) {
        webClient.put()
                .uri(buildGreenScoreUri(userId, delta))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    /**
     * Builds the URI template for incrementing green score.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param userId The user ID
     * @param delta The score increment value
     * @return The URI template string
     */
    private String buildGreenScoreUri(Long userId, int delta) {
        return baseUrl + "/users/{id}/greenscore?delta={delta}";
    }
}
