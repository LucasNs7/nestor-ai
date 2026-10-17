package com.lucas.nestor_ai.application.dto.input;

import com.lucas.nestor_ai.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;

public record TransactionInput(
        @ToolParam(description = "Descrição do gasto") String description,
        @ToolParam(description = "Valor do gasto") BigDecimal amount,
        @ToolParam(description = "Categoria do gasto") Category category
) {}
