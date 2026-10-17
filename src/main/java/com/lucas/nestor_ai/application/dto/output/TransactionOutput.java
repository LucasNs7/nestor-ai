package com.lucas.nestor_ai.application.dto.output;

import com.lucas.nestor_ai.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionOutput(
        String id,
        String description,
        BigDecimal amount,
        String category
) {

    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().toString(),
                transaction.getDescription(),
                transaction.getAmount().setScale(2, RoundingMode.HALF_UP),
                transaction.getCategory().name()
        );
    }
}
