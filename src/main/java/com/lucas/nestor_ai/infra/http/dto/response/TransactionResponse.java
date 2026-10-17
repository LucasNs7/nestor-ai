package com.lucas.nestor_ai.infra.http.dto.response;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionResponse(
        String id,
        String description,
        BigDecimal amount,
        String category
) {

    public static TransactionResponse from(TransactionOutput saveOutput) {
        return new TransactionResponse(
                saveOutput.id(),
                saveOutput.description(),
                saveOutput.amount().setScale(2, RoundingMode.HALF_UP),
                saveOutput.category()
        );
    }
}
