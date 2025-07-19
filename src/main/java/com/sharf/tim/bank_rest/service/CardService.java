package com.sharf.tim.bank_rest.service;

import com.sharf.tim.bank_rest.dto.*;
import com.sharf.tim.bank_rest.entity.Card;
import com.sharf.tim.bank_rest.entity.User;
import com.sharf.tim.bank_rest.enums.CardStatus;
import com.sharf.tim.bank_rest.exception.*;
import com.sharf.tim.bank_rest.repository.CardRepository;
import com.sharf.tim.bank_rest.repository.UserRepository;
import com.sharf.tim.bank_rest.util.CardMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper mapper;

    public PageResponse<CardAdminResponse> getAllCards(int page, int size, boolean isDescByBalance) {
        Pageable pageable;
        if (!isDescByBalance)
            pageable = PageRequest.of(page, size, Sort.by("balance").ascending());
        else
            pageable = PageRequest.of(page, size, Sort.by("balance").descending());

        Page<Card> cards = cardRepository.findAll(pageable);
        List<CardAdminResponse> cardResponses = cards.stream()
                .peek(this::isCardExpired)
                .map(mapper::toAdminResponse)
                .toList();

        return new PageResponse<>(
                cardResponses,
                cards.getNumber(),
                cards.getSize(),
                cards.getTotalElements(),
                cards.getTotalPages(),
                cards.isFirst(),
                cards.isLast());
    }

    public CardAdminResponse getCardById(Long id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        optionalCard.ifPresent(this::isCardExpired);
        return optionalCard.map(mapper::toAdminResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + id + "not found"));
    }

    public Long createCard(CreateCardRequest request) {
        boolean isCardPresent = cardRepository.findAll().stream()
                .anyMatch(card -> card.getCardNumber().equals(request.getCardNumber()));
        if (isCardPresent)
            throw new EntityExistsException("Card with number " + request.getCardNumber() + " already exist");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.getUserId() + " not found"));

        Card card = new Card();
        card.setCardNumber(request.getCardNumber());
        card.setUser(user);
        card.setStatus(request.getStatus());
        card.setBalance(request.getBalance());
        card.setExpiryDate(request.getExpiryDate());

        return cardRepository.save(card).getId();
    }

    public Long updateCardStatusById(Long id, CardStatus status) {
        return cardRepository.findById(id)
                .map(c -> {
                    c.setStatus(status);
                    return cardRepository.save(c).getId();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + id + "not found"));
    }

    public void deleteCardById(Long id) {
        cardRepository.findById(id)
                .ifPresentOrElse(cardRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Card with id " + id + "not found");
                        });
    }

    public PageResponse<CardUserResponse> getAllCardsByUserId(int page, int size, boolean isDescByBalance, Long userId) {
        Pageable pageable;
        if (isDescByBalance)
            pageable = PageRequest.of(page, size, Sort.by("balance").ascending());
        else
            pageable = PageRequest.of(page, size, Sort.by("balance").descending());

        Page<Card> cards = cardRepository.findAllByUserId(pageable, userId);
        List<CardUserResponse> cardResponses = cards.stream()
                .peek(this::isCardExpired)
                .map(mapper::toUserResponse)
                .toList();

        return new PageResponse<>(
                cardResponses,
                cards.getNumber(),
                cards.getSize(),
                cards.getTotalElements(),
                cards.getTotalPages(),
                cards.isFirst(),
                cards.isLast());
    }

    public CardUserResponse getAuthUserCardByNumber(Long userId, String number) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Optional<Card> optionalCard = Optional.ofNullable(cardRepository.findByCardNumber(number))
                .filter(u -> u.getUser().equals(user));
        optionalCard.ifPresent(this::isCardExpired);

        return optionalCard
                .map(mapper::toUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("Card with number " + number + " not found"));
    }

    @Transactional
    public TransferCardResponse makeTransferBetweenUserCards(Long userId, TransferCardRequest req) {
        if (req.getSourceCardId().equals(req.getDestinationCardId())) {
            throw new InvalidTransferException("Source card and destination card must be different");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Card source = cardRepository.findById(req.getSourceCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + req.getSourceCardId() + " not found"));

        Card destination = cardRepository.findById(req.getDestinationCardId())
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + req.getDestinationCardId() + " not found"));

        if (!source.getUser().getId().equals(user.getId()) || !destination.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("One or both cards do not belong to the user");
        }

        if (isCardExpired(source) || isCardExpired(destination)) {
            throw new ExpiredCardException("One or both cards have expired");
        }

        if (source.getStatus() != CardStatus.ACTIVE || destination.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Both cards must be active");
        }

        if (source.getBalance().compareTo(req.getSum()) < 0) {
            throw new InsufficientBalanceException("Insufficient funds on the card");
        }

        source.setBalance(source.getBalance().subtract(req.getSum()));
        destination.setBalance(destination.getBalance().add(req.getSum()));

        cardRepository.save(source);
        cardRepository.save(destination);

        return new TransferCardResponse(source.getBalance(), destination.getBalance());
    }

    public void requestBlockCard(Long cardId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        if(user.getCards().isEmpty())
            throw new ResourceNotFoundException("User with id " + userId + " has no cards");

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card with id " + cardId + "not found"));

        if (!card.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("Card does not belong to the user");
        }

        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    private boolean isCardExpired(Card card) {
        if (card.getStatus().equals(CardStatus.BLOCKED) && card.getExpiryDate().isBefore(LocalDate.now()))
            return true;
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
            return true;
        }
        return false;
    }
}
