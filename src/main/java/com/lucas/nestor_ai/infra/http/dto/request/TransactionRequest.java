package com.lucas.nestor_ai.infra.http.dto.request;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.domain.Category;

import java.math.BigDecimal;

public record TransactionRequest(
        String description,
        BigDecimal amount,
        String category
) {

    public TransactionInput toInput() {
        return new TransactionInput(
                this.description,
                this.amount,
                Category.valueOf(this.category)
        );
    }
}
