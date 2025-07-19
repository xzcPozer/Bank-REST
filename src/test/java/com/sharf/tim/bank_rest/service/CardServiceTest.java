package com.sharf.tim.bank_rest.service;

import com.sharf.tim.bank_rest.dto.*;
import com.sharf.tim.bank_rest.entity.Card;
import com.sharf.tim.bank_rest.entity.User;
import com.sharf.tim.bank_rest.enums.CardStatus;
import com.sharf.tim.bank_rest.exception.InsufficientBalanceException;
import com.sharf.tim.bank_rest.exception.InvalidTransferException;
import com.sharf.tim.bank_rest.exception.ResourceNotFoundException;
import com.sharf.tim.bank_rest.exception.UnauthorizedAccessException;
import com.sharf.tim.bank_rest.repository.CardRepository;
import com.sharf.tim.bank_rest.repository.UserRepository;
import com.sharf.tim.bank_rest.util.CardMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper mapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void getAllCards_ShouldReturnCardsAscending_WhenIsDescByBalanceTrue() {
        int page = 0;
        int size = 5;

        Card card1 = new Card();
        card1.setStatus(CardStatus.ACTIVE);
        card1.setExpiryDate(LocalDate.now().plusYears(1));
        Card card2 = new Card();
        card2.setStatus(CardStatus.ACTIVE);
        card2.setExpiryDate(LocalDate.now().plusYears(1));

        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<Card> cardPage = new PageImpl<>(List.of(card2, card1), pageable, 2);
        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(mapper.toAdminResponse(any(Card.class))).thenReturn(new CardAdminResponse());

        PageResponse<CardAdminResponse> response = cardService.getAllCards(page, size, true);

        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(1, response.getTotalPages());
        assertEquals(5, response.getSize());
        assertEquals(2, response.getTotalElements());

        verify(cardRepository).findAll(pageable);
        verify(mapper, times(2)).toAdminResponse(any(Card.class));
    }

    @Test
    void getAllCards_ShouldReturnEmptyList_WhenNoCards() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<Card> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(cardRepository.findAll(pageable)).thenReturn(emptyPage);

        PageResponse<CardAdminResponse> response = cardService.getAllCards(page, size, true);

        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());

        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getCardById_ShouldReturnCard_WhenCardExists() {
        Long id = 1L;
        Card card1 = new Card();
        card1.setStatus(CardStatus.ACTIVE);
        card1.setExpiryDate(LocalDate.now().plusYears(1));

        when(cardRepository.findById(id)).thenReturn(Optional.of(card1));
        when(mapper.toAdminResponse(card1)).thenReturn(new CardAdminResponse());

        CardAdminResponse response = cardService.getCardById(id);

        assertNotNull(response);

        verify(cardRepository).findById(id);
        verify(mapper).toAdminResponse(card1);
    }

    @Test
    void getCardById_ShouldThrowException_WhenCardNotFound() {
        Long id = 1L;
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.getCardById(id));
        verify(cardRepository).findById(id);
    }

    @Test
    void createCard_ShouldCreateCard_WhenValidRequest() {
        CreateCardRequest request = new CreateCardRequest(
                "1234 5678 9012 3456",
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.ZERO,
                1L);

        when(cardRepository.findAll()).thenReturn(List.of());
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Card savedCard = new Card();
        savedCard.setId(1L);
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);

        Long id = cardService.createCard(request);

        assertEquals(1L, id);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_ShouldThrowException_WhenCardNumberExists() {
        CreateCardRequest request = new CreateCardRequest(
                "1234 5678 9012 3456",
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.ZERO,
                1L);

        Card existingCard = new Card();
        existingCard.setCardNumber("1234 5678 9012 3456");
        when(cardRepository.findAll()).thenReturn(List.of(existingCard));

        assertThrows(EntityExistsException.class, () -> cardService.createCard(request));
    }

    @Test
    void createCard_ShouldThrowException_WhenUserNotFound() {
        CreateCardRequest request = new CreateCardRequest(
                "1234 5678 9012 3456",
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.ZERO,
                1L);

        when(cardRepository.findAll()).thenReturn(List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(request));
    }

    @Test
    void updateCardStatusById_ShouldUpdateStatus_WhenCardExists() {
        Long id = 1L;
        Card card = new Card();
        card.setId(id);

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Long updatedId = cardService.updateCardStatusById(id, CardStatus.BLOCKED);

        assertEquals(id, updatedId);
        assertEquals(CardStatus.BLOCKED, card.getStatus());

        verify(cardRepository).save(card);
    }

    @Test
    void updateCardStatusById_ShouldThrowException_WhenCardNotFound() {
        Long id = 1L;

        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.updateCardStatusById(id, CardStatus.BLOCKED));
    }

    @Test
    void deleteCardById_ShouldDeleteCard_WhenCardExists() {
        Long id = 1L;
        Card card = new Card();

        when(cardRepository.findById(id)).thenReturn(Optional.of(card));

        cardService.deleteCardById(id);

        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCardById_ShouldThrowException_WhenCardNotFound() {
        Long id = 1L;

        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cardService.deleteCardById(id));
    }

    @Test
    void getAllCardsByUserId_ShouldReturnCards_WhenUserHasCards() {
        int page = 0;
        int size = 5;
        Long userId = 1L;
        Card card1 = new Card();
        card1.setStatus(CardStatus.ACTIVE);
        card1.setExpiryDate(LocalDate.now().plusYears(1));
        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<Card> cardPage = new PageImpl<>(List.of(card1), pageable, 1);

        when(cardRepository.findAllByUserId(pageable, userId)).thenReturn(cardPage);
        when(mapper.toUserResponse(any(Card.class))).thenReturn(new CardUserResponse());

        PageResponse<CardUserResponse> response = cardService.getAllCardsByUserId(page, size, false, userId);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(cardRepository).findAllByUserId(pageable, userId);
    }

    @Test
    void getAllCardsByUserId_ShouldReturnEmptyList_WhenUserHasNoCards() {
        int page = 0;
        int size = 5;
        Long userId = 1L;
        Pageable pageable = PageRequest.of(page, size, Sort.by("balance").descending());
        Page<Card> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(cardRepository.findAllByUserId(pageable, userId)).thenReturn(emptyPage);

        PageResponse<CardUserResponse> response = cardService.getAllCardsByUserId(page, size, false, userId);

        assertNotNull(response);
        assertTrue(response.getContent().isEmpty());
    }

    @Test
    void getAuthUserCardByNumber_ShouldReturnCard_WhenCardBelongsToUser() {
        Long userId = 1L;
        String cardNumber = "1234 5678 9012 3456";
        User user = new User();
        Card card1 = new Card();
        card1.setStatus(CardStatus.ACTIVE);
        card1.setExpiryDate(LocalDate.now().plusYears(1));
        card1.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(card1);
        when(mapper.toUserResponse(card1)).thenReturn(new CardUserResponse());

        CardUserResponse response = cardService.getAuthUserCardByNumber(userId, cardNumber);

        assertNotNull(response);
        verify(userRepository).findById(userId);
        verify(cardRepository).findByCardNumber(cardNumber);
    }

    @Test
    void getAuthUserCardByNumber_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        String cardNumber = "1234 5678 9012 3456";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardService.getAuthUserCardByNumber(userId, cardNumber));
    }

    @Test
    void getAuthUserCardByNumber_ShouldThrowException_WhenCardNotBelongsToUser() {
        Long userId = 1L;
        String cardNumber = "1234 5678 9012 3456";
        User user = new User();
        Card card = new Card();
        card.setUser(new User());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findByCardNumber(cardNumber)).thenReturn(card);

        assertThrows(EntityNotFoundException.class, () -> cardService.getAuthUserCardByNumber(userId, cardNumber));
    }

    @Test
    void makeTransferBetweenUserCards_ShouldTransfer_WhenValidRequest() {
        Long userId = 1L;
        TransferCardRequest request = new TransferCardRequest(1L, 2L, BigDecimal.valueOf(100));
        User user = new User();
        user.setId(userId);
        Card source = new Card();
        source.setUser(user);
        source.setBalance(BigDecimal.valueOf(200));
        source.setStatus(CardStatus.ACTIVE);
        source.setExpiryDate(LocalDate.now().plusDays(1));
        Card destination = new Card();
        destination.setUser(user);
        destination.setBalance(BigDecimal.valueOf(50));
        destination.setStatus(CardStatus.ACTIVE);
        destination.setExpiryDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(source));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(destination));

        TransferCardResponse response = cardService.makeTransferBetweenUserCards(userId, request);

        assertEquals(BigDecimal.valueOf(100), response.getSourceCardBalance());
        assertEquals(BigDecimal.valueOf(150), response.getDestinationCardBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void makeTransferBetweenUserCards_ShouldThrowException_WhenSameCards() {
        Long userId = 1L;
        TransferCardRequest request = new TransferCardRequest(1L, 1L, BigDecimal.valueOf(100));

        assertThrows(InvalidTransferException.class, () -> cardService.makeTransferBetweenUserCards(userId, request));
    }

    @Test
    void makeTransferBetweenUserCards_ShouldThrowException_WhenInsufficientBalance() {
        Long userId = 1L;
        TransferCardRequest request = new TransferCardRequest(1L, 2L, BigDecimal.valueOf(300));
        User user = new User();
        user.setId(userId);
        Card source = new Card();
        source.setUser(user);
        source.setBalance(BigDecimal.valueOf(200));
        source.setStatus(CardStatus.ACTIVE);
        source.setExpiryDate(LocalDate.now().plusDays(1));
        Card destination = new Card();
        destination.setUser(user);
        destination.setStatus(CardStatus.ACTIVE);
        destination.setExpiryDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(source));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(destination));

        assertThrows(InsufficientBalanceException.class, () -> cardService.makeTransferBetweenUserCards(userId, request));
    }

    @Test
    void requestBlockCard_ShouldBlockCard_WhenValidRequest() {
        Long cardId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setCards(List.of(new Card()));
        Card card = new Card();
        card.setId(cardId);
        card.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        cardService.requestBlockCard(cardId, userId);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlockCard_ShouldThrowException_WhenUserHasNoCards() {
        Long cardId = 1L;
        Long userId = 1L;
        User user = new User();
        user.setCards(List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(ResourceNotFoundException.class, () -> cardService.requestBlockCard(cardId, userId));
    }

    @Test
    void requestBlockCard_ShouldThrowException_WhenCardNotBelongsToUser() {
        Long cardId = 1L;
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setCards(List.of(new Card()));

        Card card = new Card();
        card.setId(cardId);
        User userCard = new User();
        userCard.setId(2L);
        card.setUser(userCard);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThrows(UnauthorizedAccessException.class, () -> cardService.requestBlockCard(cardId, userId));
    }
}