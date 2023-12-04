package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.pojo.ApiError;
import com.hoquangnam45.pharmacy.pojo.UserProfile;
import com.hoquangnam45.pharmacy.service.UserService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable("userId") UUID userId) {
        return Optional.ofNullable(userService.getUserProfile(userId))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ApiError.notFound("User profile not found"));
    }
}
