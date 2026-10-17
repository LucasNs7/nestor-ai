package com.lucas.nestor_ai.infra.persistence.entity;

import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    private UUID id;
    private String description;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static TransactionEntity from(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId().transactionId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory()
        );
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.id),
                this.getDescription(),
                this.getAmount(),
                this.getCategory()
        );
    }
}
