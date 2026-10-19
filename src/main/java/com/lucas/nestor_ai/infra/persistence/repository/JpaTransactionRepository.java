package com.lucas.nestor_ai.infra.persistence.repository;

import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionId;
import com.lucas.nestor_ai.domain.TransactionRepository;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionEntityRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);

        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return repository.findById(id.transactionId())
                .map(TransactionEntity::toDomain);
    }

    @Override
    public List<Transaction> findAllByDate(LocalDate date) {
        return repository.findAllByDate(date).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        return repository.findAllByCategory(category).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByDateBetween(LocalDate start, LocalDate end) {
        return repository.findAllByDateBetween(start, end).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByType(TransactionType type) {
        return repository.findAllByType(type).stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(TransactionId id) {
        repository.deleteById(id.transactionId());
    }
}
