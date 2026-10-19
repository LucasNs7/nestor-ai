package com.lucas.nestor_ai.domain;

import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    List<Transaction> findAllByType(TransactionType type);

    List<Transaction> findAllByDateBetween(LocalDate start, LocalDate end);

    List<Transaction> findAllByDate(LocalDate date);

    Optional<Transaction> findById(TransactionId id);

    void deleteById(TransactionId id);
}
