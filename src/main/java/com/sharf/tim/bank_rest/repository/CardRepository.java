package com.sharf.tim.bank_rest.repository;

import com.sharf.tim.bank_rest.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByUserId(Pageable pageable, Long id);
    Card findByCardNumber(String cardNum);
}
