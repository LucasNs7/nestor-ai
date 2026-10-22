package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTransactionsByDateUseCase {

    private final TransactionRepository repository;

    public List<TransactionOutput> execute(LocalDate date){
        return repository.findAllByDate(date).stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
