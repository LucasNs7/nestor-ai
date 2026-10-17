package com.lucas.nestor_ai.application.service;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveTransactionUseCase {

    private final TransactionRepository repository;

    @Tool(name = "save-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(TransactionInput input) {
        var transaction = new Transaction(input.description(), input.amount(), input.category());

        return TransactionOutput.from(repository.save(transaction));
    }
}
