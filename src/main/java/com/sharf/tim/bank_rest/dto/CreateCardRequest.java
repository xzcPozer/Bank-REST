package com.sharf.tim.bank_rest.dto;

import com.sharf.tim.bank_rest.enums.CardStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateCardRequest {
    @NotNull(message = "Enter the card number")
    @Pattern(regexp = "^\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}$",
            message = "Card number must be the format: 9999 9999 9999 9999")
    private String cardNumber;

    @NotNull(message = "Enter the expiry date")
    @Future
    private LocalDate expiryDate;

    @NotNull(message = "Enter the status")
    private CardStatus status;

    @NotNull(message = "Enter the balance")
    @Positive(message = "The balance must be positive")
    private BigDecimal balance;

    @NotNull(message = "Enter the user id")
    private Long userId;
}
