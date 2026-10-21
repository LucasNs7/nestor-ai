package com.lucas.nestor_ai.infra.http.dto.response.transaction;

import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String description,
        BigDecimal amount,
        String category,
        String type,
        LocalDate date,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TransactionResponse from(TransactionOutput saveOutput) {
        return new TransactionResponse(
                saveOutput.id(),
                saveOutput.description(),
                saveOutput.amount(),
                saveOutput.category(),
                saveOutput.type(),
                saveOutput.date(),
                saveOutput.createdAt(),
                saveOutput.updatedAt()
        );
    }
}
