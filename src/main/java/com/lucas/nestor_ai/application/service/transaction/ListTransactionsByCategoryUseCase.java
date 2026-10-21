package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
import com.lucas.nestor_ai.domain.TransactionRepository;
import com.lucas.nestor_ai.domain.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTransactionsByCategoryUseCase {

    private final TransactionRepository repository;

    public List<TransactionOutput> execute(Category category) {
        return repository.findAllByCategory(category).stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
