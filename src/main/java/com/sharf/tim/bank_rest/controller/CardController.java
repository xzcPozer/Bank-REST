package com.sharf.tim.bank_rest.controller;

import com.sharf.tim.bank_rest.dto.*;
import com.sharf.tim.bank_rest.enums.CardStatus;
import com.sharf.tim.bank_rest.security.user.CardUserDetails;
import com.sharf.tim.bank_rest.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("cards")
@RestController
@Tag(name = "Card")
public class CardController {

    private final CardService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<CardAdminResponse>> getAllCards(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false) boolean isDescByBalance
    ) {
        return ResponseEntity.ok(service.getAllCards(page,size,isDescByBalance));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CardAdminResponse> getCardById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getCardById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> addCard(
            @RequestBody @Valid CreateCardRequest request
    ) {
        return ResponseEntity.ok(service.createCard(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Long> updateCardStatusById(
            @PathVariable Long id,
            @RequestParam CardStatus status
    ) {
        return ResponseEntity.ok(service.updateCardStatusById(id, status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCardById(
            @PathVariable Long id
    ) {
        service.deleteCardById(id);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-cards")
    public ResponseEntity<PageResponse<CardUserResponse>> getCardsByConnectedUser(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false) boolean isDescByBalance,
            Authentication user
            ) {
        Long userId = ((CardUserDetails) user.getPrincipal()).getId();

        return ResponseEntity.ok(service.getAllCardsByUserId(page,size,isDescByBalance, userId));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/by/number")
    public ResponseEntity<CardUserResponse> getUserCardByNumber(
            @RequestParam String number,
            Authentication user
    ) {
        Long userId = ((CardUserDetails) user.getPrincipal()).getId();

        return ResponseEntity.ok(service.getAuthUserCardByNumber(userId, number));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<TransferCardResponse> makeTransferBetweenUserCards(
            @RequestBody @Valid TransferCardRequest req,
            Authentication user
    ) {
        Long userId = ((CardUserDetails) user.getPrincipal()).getId();

        return ResponseEntity.ok(service.makeTransferBetweenUserCards(userId, req));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/block")
    public ResponseEntity<String> requestBlockCard(
            @RequestParam Long cardId,
            Authentication user) {
        Long userId = ((CardUserDetails) user.getPrincipal()).getId();

        service.requestBlockCard(cardId, userId);
        return ResponseEntity.ok("Card blocking request sent successfully");
    }
}
