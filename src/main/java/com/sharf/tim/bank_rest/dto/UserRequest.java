package com.sharf.tim.bank_rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "Enter the name")
        String firstName,

        @NotNull(message = "Enter the lastname")
        String lastname,

        @Email(message = "Wrong email format")
        @NotNull(message = "Enter the email")
        String email,

        @NotNull(message = "Enter the password")
        String password
        ) {
}
