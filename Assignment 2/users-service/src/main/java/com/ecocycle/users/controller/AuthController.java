package com.ecocycle.users.controller;

import com.ecocycle.common.security.JwtUtil;
import com.ecocycle.users.model.User;
import com.ecocycle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repo;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username,
                                           @RequestParam String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        repo.save(u);

        String token = new JwtUtil(secret, expiration).generateToken(u.getId());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email) {
        User u = repo.findAll().stream()
                .filter(x -> x.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = new JwtUtil(secret, expiration).generateToken(u.getId());
        return ResponseEntity.ok(token);
    }
}
