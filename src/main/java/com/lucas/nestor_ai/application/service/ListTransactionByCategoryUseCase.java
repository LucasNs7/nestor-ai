package com.lucas.nestor_ai.application.service;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListTransactionByCategoryUseCase {

    private final TransactionRepository repository;

    @Tool(name = "list-transactions-by-category", description = "Lista transações financeiras por categoria")
    public List<TransactionOutput> execute(Category category) {
        return repository.findAllByCategory(category).stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
