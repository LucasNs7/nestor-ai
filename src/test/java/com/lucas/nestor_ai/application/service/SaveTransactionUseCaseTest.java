package com.lucas.nestor_ai.application.service;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveTransactionUseCaseTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private SaveTransactionUseCase useCase;

    @Test
    void deveSalvarTransacaoRetornandoOutput() {
        var input = new TransactionInput(
                "Compra no mercado",
                new BigDecimal("125.50"),
                Category.GROCERIES
        );

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransactionOutput output = useCase.execute(input);

        assertNotNull(output.id());
        assertEquals("Compra no mercado", output.description());
        assertEquals(new BigDecimal("125.50"), output.amount());
        assertEquals("GROCERIES", output.category());

        verify(repository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deveEncaminharOsDadosCorretosAoRepositorio() {
        var input = new TransactionInput(
                "Farmácia",
                new BigDecimal("32.90"),
                Category.PHARMA
        );

        when(repository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(input);

        verify(repository).save(argThat(transaction ->
                transaction.getDescription().equals("Farmácia")
                        && transaction.getAmount()
                        .compareTo(new BigDecimal("32.90")) == 0
                        && transaction.getCategory() == Category.PHARMA
        ));
    }
}
