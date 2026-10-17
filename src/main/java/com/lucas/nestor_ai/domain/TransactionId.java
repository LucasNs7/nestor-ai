package com.lucas.nestor_ai.domain;

import java.util.UUID;

public record TransactionId(UUID transactionId) {

    public TransactionId() {
        this(UUID.randomUUID());
    }
}
