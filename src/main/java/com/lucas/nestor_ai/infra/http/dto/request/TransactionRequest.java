package com.lucas.nestor_ai.infra.http.dto.request;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
        String description,

        @NotNull(message = "O valor é obrigatório")
        @DecimalMin(
                value = "0.00",
                inclusive = false,
                message = "O valor deve ser maior que zero")
        BigDecimal amount,

        @NotNull(message = "A categoria é obrigatória")
        Category category,

        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "A data da transação é obrigatória")
        LocalDate date
) {

    public TransactionInput toInput() {
        return new TransactionInput(
                this.description,
                this.amount,
                this.category,
                this.type,
                this.date
        );
    }
}
