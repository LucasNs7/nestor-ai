package com.lucas.nestor_ai.infra.http.dto.request;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionPatchRequest(

        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String description,

        @DecimalMin(
                value = "0.00",
                inclusive = false,
                message = "O valor deve ser maior que zero")
        BigDecimal amount,

        Category category,

        TransactionType type,

        LocalDate date
) {

    public TransactionInput toInput() {

        if (this.description != null && this.description.trim().isEmpty()){
            return new TransactionInput(
                    null,
                    this.amount,
                    this.category,
                    this.type,
                    this.date
            );
        }

        return new TransactionInput(
                this.description,
                this.amount,
                this.category,
                this.type,
                this.date
        );
    }
}
