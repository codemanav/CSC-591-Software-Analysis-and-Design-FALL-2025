package com.ecocycle.users.dto;

import com.ecocycle.users.model.User;

public record UserDto(Long id, String username, String email, int greenScore, boolean isVerifier) {
    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getGreenScore(), u.isVerifier());
    }
}
