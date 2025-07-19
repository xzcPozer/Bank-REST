package com.sharf.tim.bank_rest.service;

import com.sharf.tim.bank_rest.dto.LoginRequest;
import com.sharf.tim.bank_rest.dto.PageResponse;
import com.sharf.tim.bank_rest.dto.UserRequest;
import com.sharf.tim.bank_rest.dto.UserResponse;
import com.sharf.tim.bank_rest.entity.Role;
import com.sharf.tim.bank_rest.entity.User;
import com.sharf.tim.bank_rest.exception.ResourceNotFoundException;
import com.sharf.tim.bank_rest.repository.RoleRepository;
import com.sharf.tim.bank_rest.repository.UserRepository;
import com.sharf.tim.bank_rest.util.JwtUtils;
import com.sharf.tim.bank_rest.util.UserMapper;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// todo: write methods

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper mapper;

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateTokenForUser(authentication);
    }

    public Long createUser(UserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.email()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.email());
                    user.setPassword(passwordEncoder.encode(request.password()));
                    user.setFirstName(request.firstName());
                    user.setLastName(request.lastname());
                    user.getRoles().add(getRoleByName("ROLE_USER"));
                    return userRepository.save(user).getId();
                }).orElseThrow(() -> new EntityExistsException("User with email " + request.email() + "already exist"));
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + "not found"));
    }

    public void deleteUserById(Long id){
        userRepository.findById(id)
                .ifPresentOrElse(userRepository::delete,
                        ()->{throw new ResourceNotFoundException("User with id " + id + "not found");});
    }

    public PageResponse<UserResponse> getAllUsers(int page, int size, boolean isAsc) {
        Pageable pageable;
        if (isAsc)
            pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        else
            pageable = PageRequest.of(page, size, Sort.by("lastName").descending());

        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> usersResponse = users.stream()
                .map(mapper::toResponse)
                .toList();

        return new PageResponse<>(
                usersResponse,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast());
    }

    private Role getRoleByName(String name) {
        return Optional.ofNullable(roleRepository.findByName(name))
                .orElseThrow(() -> new ResourceNotFoundException("Role " + name + " not found"));
    }
}
