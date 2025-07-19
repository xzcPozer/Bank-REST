package com.sharf.tim.bank_rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferCardRequest {

    @NotNull(message = "Enter the source card id")
    private Long sourceCardId;

    @NotNull(message = "Enter the destination card id")
    private Long destinationCardId;

    @NotNull(message = "Enter the balance")
    @Positive(message = "The balance must be positive")
    private BigDecimal sum;
}
