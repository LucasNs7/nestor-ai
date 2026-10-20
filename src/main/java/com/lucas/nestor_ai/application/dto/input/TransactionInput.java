package com.lucas.nestor_ai.application.dto.input;

import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionInput(
        String description,
        BigDecimal amount,
        Category category,
        TransactionType type,
        LocalDate date
) {

    public Transaction toDomain() {
        return new Transaction(
                this.description,
                this.amount,
                this.category,
                this.type,
                this.date
        );
    }
}
