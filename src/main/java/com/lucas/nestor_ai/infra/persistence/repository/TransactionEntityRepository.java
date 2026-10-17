package com.lucas.nestor_ai.infra.persistence.repository;

import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.infra.persistence.entity.TransactionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionEntityRepository extends CrudRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findAllByCategory(Category category);
}
