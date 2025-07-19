package com.sharf.tim.bank_rest.controller;

import com.sharf.tim.bank_rest.dto.LoginRequest;
import com.sharf.tim.bank_rest.dto.PageResponse;
import com.sharf.tim.bank_rest.dto.UserRequest;
import com.sharf.tim.bank_rest.dto.UserResponse;
import com.sharf.tim.bank_rest.security.user.CardUserDetails;
import com.sharf.tim.bank_rest.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("users")
@RestController
@Tag(name = "User")
public class UserController {

    private final UserService service;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping
    public ResponseEntity<Long> addUser(
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(service.createUser(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addUser(
            @PathVariable Long id
    ) {
        service.deleteUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-profile")
    public ResponseEntity<UserResponse> getAuthUser(
            Authentication user
    ) {
        Long userId = ((CardUserDetails) user.getPrincipal()).getId();

        return ResponseEntity.ok(service.getUserById(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false) boolean isAsc
    ) {
        return ResponseEntity.ok(service.getAllUsers(page, size, isAsc));
    }

}
