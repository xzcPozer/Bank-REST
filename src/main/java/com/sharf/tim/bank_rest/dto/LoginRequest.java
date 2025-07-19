package com.sharf.tim.bank_rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Wrong email format")
    @NotEmpty(message = "Enter the email")
    @NotNull(message = "Enter the email")
    private String email;

    @NotEmpty(message = "Enter the password")
    @NotNull(message = "Enter the password")
    private String password;
}
