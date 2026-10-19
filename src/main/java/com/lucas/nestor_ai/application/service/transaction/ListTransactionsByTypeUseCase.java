package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.TransactionRepository;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTransactionsByTypeUseCase {

    private final TransactionRepository repository;

    public List<TransactionOutput> execute(TransactionType type) {
        return repository.findAllByType(type).stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
