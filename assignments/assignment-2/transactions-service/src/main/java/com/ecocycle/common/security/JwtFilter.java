package com.ecocycle.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // Refactoring: Extract Constant - Removes Magic Number smell (substring(7) for "Bearer " prefix)
    private static final int BEARER_PREFIX_LENGTH = 7;
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtFilter(@Value("${jwt.secret}") String secret,
                     @Value("${jwt.expiration}") long expiration) {
        this.jwtUtil = new JwtUtil(secret, expiration);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!processJwtToken(request, response)) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the given path is a public endpoint that doesn't require JWT.
     * Refactoring: Extract Method - Reduces Complex Conditional and Long Statement smells.
     * 
     * @param path The request path
     * @return true if the path is a public endpoint
     */
    private boolean isPublicEndpoint(String path) {
        return isAuthEndpoint(path) ||
                isApiDocsEndpoint(path) ||
                isSwaggerEndpoint(path) ||
                isErrorEndpoint(path);
    }

    /**
     * Checks if the path is an authentication endpoint.
     * 
     * @param path The request path
     * @return true if the path starts with "/auth"
     */
    private boolean isAuthEndpoint(String path) {
        return path.startsWith("/auth");
    }

    /**
     * Checks if the path is an API documentation endpoint.
     * 
     * @param path The request path
     * @return true if the path starts with "/v3/api-docs"
     */
    private boolean isApiDocsEndpoint(String path) {
        return path.startsWith("/v3/api-docs");
    }

    /**
     * Checks if the path is a Swagger UI endpoint.
     * 
     * @param path The request path
     * @return true if the path starts with "/swagger-ui" or "/swagger-resources"
     */
    private boolean isSwaggerEndpoint(String path) {
        return path.startsWith("/swagger-ui") || path.startsWith("/swagger-resources");
    }

    /**
     * Checks if the path is the error endpoint.
     * 
     * @param path The request path
     * @return true if the path equals "/error"
     */
    private boolean isErrorEndpoint(String path) {
        return path.equals("/error");
    }

    /**
     * Processes the JWT token from the request and sets the userId attribute.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return true if token processing was successful, false otherwise
     */
    private boolean processJwtToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader("Authorization");
        
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Token");
            return false;
        }

        String token = extractTokenFromHeader(header);
        return validateAndSetUserId(request, response, token);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * Refactoring: Extract Method - Removes Magic Number smell.
     * 
     * @param header The Authorization header value
     * @return The JWT token without the "Bearer " prefix
     */
    private String extractTokenFromHeader(String header) {
        return header.substring(BEARER_PREFIX_LENGTH);
    }

    /**
     * Validates the JWT token and sets the userId attribute on the request.
     * Refactoring: Extract Method - Reduces Long Statement smell.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param token The JWT token to validate
     * @return true if validation was successful, false otherwise
     */
    private boolean validateAndSetUserId(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {
        try {
            Long userId = jwtUtil.validateAndExtractUserId(token);
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return false;
        }
    }

}
