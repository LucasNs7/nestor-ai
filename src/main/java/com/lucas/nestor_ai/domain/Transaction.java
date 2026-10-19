package com.lucas.nestor_ai.domain;

import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public class Transaction {

    private TransactionId id;
    private String description;
    private BigDecimal amount;
    private Category category;
    private TransactionType type;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transaction(
            String description,
            BigDecimal amount,
            Category category,
            TransactionType type,
            LocalDate date
    ) {
        this.id = new TransactionId();
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Transaction updateFields(
            String description,
            BigDecimal amount,
            Category category,
            TransactionType type,
            LocalDate date
    ) {
        boolean changed =
                updateIfChanged(description, this::getDescription, this::setDescription)
                | updateIfChanged(amount, this::getAmount, this::setAmount)
                | updateIfChanged(category, this::getCategory, this::setCategory)
                | updateIfChanged(type, this::getType, this::setType)
                | updateIfChanged(date, this::getDate, this::setDate);

        if (changed) this.setUpdatedAt(LocalDateTime.now());

        return this;
    }

    private <T> boolean updateIfChanged(T newValue, Supplier<T> getter, Consumer<T> setter) {
        if (newValue != null && !Objects.equals(getter.get(), newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    private void setCategory(Category category) {
        this.category = category;
    }

    private void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private void setType(TransactionType type) {
        this.type = type;
    }

    private void setDate(LocalDate date) {
        this.date = date;
    }
}
