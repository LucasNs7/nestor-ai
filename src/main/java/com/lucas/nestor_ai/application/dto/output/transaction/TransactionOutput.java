package com.lucas.nestor_ai.application.dto.output.transaction;

import com.lucas.nestor_ai.domain.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionOutput(
        String id,
        String description,
        BigDecimal amount,
        String category,
        String type,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().transactionId().toString(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory().name(),
                transaction.getType().name(),
                transaction.getDate(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
