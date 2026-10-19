package com.lucas.nestor_ai.application.service.helper;

import com.lucas.nestor_ai.application.exception.ResourceNotFoundException;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionId;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionFinderHelper {

    private final TransactionRepository repository;

    public Transaction findByIdOrThrowResourceNotFound(TransactionId id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Transação não encontrada com id: " + id)
        );
    }
}
