package com.lucas.nestor_ai.domain;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    List<Transaction> findAllByCategory(Category category);
}
