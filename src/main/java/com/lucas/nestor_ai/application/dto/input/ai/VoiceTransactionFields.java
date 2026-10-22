package com.lucas.nestor_ai.application.dto.input.ai;

import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VoiceTransactionFields(
        String description,
        BigDecimal amount,
        Category category,
        TransactionType type,
        LocalDate date
) {}
