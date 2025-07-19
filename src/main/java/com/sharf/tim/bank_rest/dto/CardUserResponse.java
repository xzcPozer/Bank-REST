package com.sharf.tim.bank_rest.dto;

import com.sharf.tim.bank_rest.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardUserResponse {
    private Long id;
    private String cardNumber;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
}
