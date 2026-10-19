
package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListTransactionsByPeriodUseCase {

    private final TransactionRepository repository;

    public List<TransactionOutput> execute(LocalDate start, LocalDate end) {

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior ou igual à data final."
            );
        }

        return repository.findAllByDateBetween(start, end)
                .stream()
                .map(TransactionOutput::from)
                .toList();
    }
}
