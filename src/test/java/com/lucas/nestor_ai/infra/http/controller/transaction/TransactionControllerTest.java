package com.lucas.nestor_ai.infra.http.controller.transaction;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.application.service.transaction.*;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionPatchRequest;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private ListTransactionsByCategoryUseCase listByCategoryUseCase;

    @Mock
    private ListTransactionsByPeriodUseCase listByPeriodUseCase;

    @Mock
    private ListTransactionsByDateUseCase listByDateUseCase;

    @Mock
    private ListTransactionsByTypeUseCase listByTypeUseCase;

    @Mock
    private DeleteTransactionByIdUseCase deleteByIdUseCase;

    @Mock
    private UpdateTransactionUseCase updateUseCase;

    @Mock
    private SaveTransactionUseCase saveUseCase;

    private TransactionController controller;

    private Category category;
    private TransactionType type;
    private LocalDate date;
    private TransactionOutput output;

    @BeforeEach
    void setUp() {
        controller = new TransactionController(
                listByCategoryUseCase,
                listByPeriodUseCase,
                listByDateUseCase,
                listByTypeUseCase,
                deleteByIdUseCase,
                updateUseCase,
                saveUseCase
        );

        category = Category.values()[0];
        type = TransactionType.values()[0];
        date = LocalDate.of(2026, 10, 19);

        Transaction transaction = new Transaction(
                "Compra de teste",
                new BigDecimal("50.00"),
                category,
                type,
                date
        );

        output = TransactionOutput.from(transaction);
    }

    @Test
    void createTransactionShouldReturnResponse() {
        TransactionRequest request = new TransactionRequest(
                "Compra de teste",
                new BigDecimal("50.00"),
                category,
                type,
                date
        );

        when(saveUseCase.execute(request.toInput())).thenReturn(output);

        var response = controller.createTransaction(request);

        assertNotNull(response);
        verify(saveUseCase).execute(request.toInput());
    }

    @Test
    void findByCategoryShouldReturnResponses() {
        when(listByCategoryUseCase.execute(category)).thenReturn(List.of(output));

        var response = controller.findAllTransactionsByCategory(category);

        assertEquals(1, response.size());
        verify(listByCategoryUseCase).execute(category);
    }

    @Test
    void findByTypeShouldReturnResponses() {
        when(listByTypeUseCase.execute(type)).thenReturn(List.of(output));

        var response = controller.findAllTransactionsByType(type);

        assertEquals(1, response.size());
        verify(listByTypeUseCase).execute(type);
    }

    @Test
    void findByDateShouldReturnResponses() {
        when(listByDateUseCase.execute(date)).thenReturn(List.of(output));

        var response = controller.findAllTransactionsByDate(date);

        assertEquals(1, response.size());
        verify(listByDateUseCase).execute(date);
    }

    @Test
    void findByPeriodShouldReturnResponses() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 20);

        when(listByPeriodUseCase.execute(start, end)).thenReturn(List.of(output));

        var response = controller.findAllTransactionsByPeriod(start, end);

        assertEquals(1, response.size());
        verify(listByPeriodUseCase).execute(start, end);
    }

    @Test
    void updateTransactionShouldReturnResponse() {
        UUID id = UUID.randomUUID();

        TransactionPatchRequest request = new TransactionPatchRequest(
                "Compra atualizada",
                new BigDecimal("75.00"),
                category,
                type,
                date
        );

        when(updateUseCase.execute(id, request.toInput())).thenReturn(output);

        var response = controller.updateTransaction(id, request);

        assertNotNull(response);
        verify(updateUseCase).execute(id, request.toInput());
    }

    @Test
    void deleteTransactionShouldReturnNoContent() {
        UUID id = UUID.randomUUID();

        var response = controller.deleteTransaction(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(deleteByIdUseCase).execute(id);
    }

    @Test
    void findByCategoryShouldReturnEmptyListWhenNoTransactionsExist() {
        when(listByCategoryUseCase.execute(category)).thenReturn(List.of());

        var response = controller.findAllTransactionsByCategory(category);

        assertTrue(response.isEmpty());
    }
}