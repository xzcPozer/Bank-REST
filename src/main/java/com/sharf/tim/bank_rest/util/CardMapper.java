package com.sharf.tim.bank_rest.util;

import com.sharf.tim.bank_rest.dto.CardAdminResponse;
import com.sharf.tim.bank_rest.dto.CardUserResponse;
import com.sharf.tim.bank_rest.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardAdminResponse toAdminResponse(Card card){
        return new CardAdminResponse(
                card.getId(),
                getMaskedCard(card.getCardNumber()),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance(),
                card.getUser().getId()
        );
    }

    public CardUserResponse toUserResponse(Card card){
        return new CardUserResponse(
                card.getId(),
                card.getCardNumber(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }

    private String getMaskedCard(String cardNumber) {
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
