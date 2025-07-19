package com.sharf.tim.bank_rest.entity;

import com.sharf.tim.bank_rest.enums.CardStatus;
import com.sharf.tim.bank_rest.util.StringCryptoConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "card_number", nullable = false)
    @Convert(converter = StringCryptoConverter.class)
    private String cardNumber;
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
