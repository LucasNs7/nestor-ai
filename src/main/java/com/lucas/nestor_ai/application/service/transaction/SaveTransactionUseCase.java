package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.input.transaction.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SaveTransactionUseCase {

    private final TransactionRepository repository;

    public TransactionOutput execute(TransactionInput input) {
        var transaction = input.toDomain();

        return TransactionOutput.from(repository.save(transaction));
    }
}
