package com.ecocycle.users.controller;

import com.ecocycle.users.dto.CreateUserRequest;
import com.ecocycle.users.dto.UserDto;
import com.ecocycle.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping
    public List<UserDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}/greenscore")
    public UserDto updateScore(@PathVariable Long id, @RequestParam(defaultValue = "1") int delta) {
        return service.incrementGreenScore(id, delta);
    }
}
