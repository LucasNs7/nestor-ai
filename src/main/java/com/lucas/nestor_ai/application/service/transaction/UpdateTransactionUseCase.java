package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.input.transaction.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
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
public class UpdateTransactionUseCase {

    private final TransactionFinderHelper finder;
    private final TransactionRepository repository;

    public TransactionOutput execute(UUID id, TransactionInput input) {

        var transactionId = new TransactionId(id);

        var entity = finder.findByIdOrThrowResourceNotFound(transactionId);

        entity.updateFields(
                input.description(),
                input.amount(),
                input.category(),
                input.type(),
                input.date()
        );

        return TransactionOutput.from(repository.save(entity));
    }
}
