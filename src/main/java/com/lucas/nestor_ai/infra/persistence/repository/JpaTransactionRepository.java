package com.lucas.nestor_ai.infra.persistence.repository;

import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionRepository;
import com.lucas.nestor_ai.infra.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionEntityRepository entityRepository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);

        return entityRepository.save(entity).toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return entityRepository.findAllByCategory(category).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }
}
