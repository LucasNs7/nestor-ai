package com.lucas.nestor_ai.infra.persistence.repository;

import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findAllByCategory(Category category);

    List<TransactionEntity> findAllByType(TransactionType type);

    List<TransactionEntity> findAllByDateBetween(LocalDate start, LocalDate end);

    List<TransactionEntity> findAllByDate(LocalDate date);
}
