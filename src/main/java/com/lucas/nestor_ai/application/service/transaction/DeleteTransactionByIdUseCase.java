package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.service.helper.TransactionFinderHelper;
import com.lucas.nestor_ai.domain.TransactionId;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTransactionByIdUseCase {

    private final TransactionFinderHelper finder;
    private final TransactionRepository repository;

    public void execute(UUID id) {
        var transactionId = new TransactionId(id);

        finder.findByIdOrThrowResourceNotFound(transactionId);

        repository.deleteById(transactionId);
    }
}
