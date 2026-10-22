package com.lucas.nestor_ai.application.service.transaction;

import com.lucas.nestor_ai.application.dto.input.transaction.TransactionInput;
import com.lucas.nestor_ai.application.service.helper.TransactionFinderHelper;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionRepository;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionUseCasesTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionFinderHelper finder;

    private Category category;
    private TransactionType type;
    private LocalDate date;
    private Transaction transaction;
    private TransactionInput input;

    @BeforeEach
    void setUp() {
        category = Category.values()[0];
        type = TransactionType.values()[0];
        date = LocalDate.of(2026, 10, 19);

        transaction = new Transaction(
                "Compra de teste",
                new BigDecimal("50.00"),
                category,
                type,
                date
        );

        input = new TransactionInput(
                "Compra de teste",
                new BigDecimal("50.00"),
                category,
                type,
                date
        );
    }

    @Test
    void saveTransactionShouldSaveAndReturnOutput() {
        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var useCase = new SaveTransactionUseCase(repository);

        var result = useCase.execute(input);

        assertNotNull(result);
        verify(repository).save(any(Transaction.class));
    }

    @Test
    void listByCategoryShouldReturnMatchingTransactions() {
        when(repository.findAllByCategory(category)).thenReturn(List.of(transaction));

        var useCase = new ListTransactionsByCategoryUseCase(repository);

        var result = useCase.execute(category);

        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        verify(repository).findAllByCategory(category);
    }

    @Test
    void listByTypeShouldReturnMatchingTransactions() {
        when(repository.findAllByType(type)).thenReturn(List.of(transaction));

        var useCase = new ListTransactionsByTypeUseCase(repository);

        var result = useCase.execute(type);

        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        verify(repository).findAllByType(type);
    }

    @Test
    void listByDateShouldReturnTransactionsFromDate() {
        when(repository.findAllByDate(date)).thenReturn(List.of(transaction));

        var useCase = new ListTransactionsByDateUseCase(repository);

        var result = useCase.execute(date);

        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        verify(repository).findAllByDate(date);
    }

    @Test
    void listByPeriodShouldReturnTransactionsWithinPeriod() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 20);

        when(repository.findAllByDateBetween(start, end)).thenReturn(List.of(transaction));

        var useCase = new ListTransactionsByPeriodUseCase(repository);

        var result = useCase.execute(start, end);

        assertEquals(1, result.size());
        verify(repository).findAllByDateBetween(start, end);
    }

    @Test
    void listByPeriodShouldRejectStartAfterEnd() {
        LocalDate start = LocalDate.of(2026, 10, 20);
        LocalDate end = LocalDate.of(2026, 10, 1);

        var useCase = new ListTransactionsByPeriodUseCase(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(start, end));

        verifyNoInteractions(repository);
    }

    @Test
    void listByPeriodShouldRejectEqualDates() {
        LocalDate sameDate = LocalDate.of(2026, 10, 19);

        var useCase = new ListTransactionsByPeriodUseCase(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(sameDate, sameDate));

        verifyNoInteractions(repository);
    }

    @Test
    void updateTransactionShouldFindUpdateAndSave() {
        UUID id = UUID.randomUUID();

        when(finder.findByIdOrThrowResourceNotFound(any())).thenReturn(transaction);

        when(repository.save(transaction)).thenReturn(transaction);

        var useCase = new UpdateTransactionUseCase(finder, repository);

        var result = useCase.execute(id, input);

        assertNotNull(result);
        verify(finder).findByIdOrThrowResourceNotFound(any());
        verify(repository).save(transaction);
    }

    @Test
    void deleteTransactionShouldFindAndDelete() {
        UUID id = UUID.randomUUID();

        when(finder.findByIdOrThrowResourceNotFound(any())).thenReturn(transaction);

        var useCase = new DeleteTransactionByIdUseCase(finder, repository);

        assertDoesNotThrow(() -> useCase.execute(id));

        verify(finder).findByIdOrThrowResourceNotFound(any());
        verify(repository).deleteById(any());
    }
}