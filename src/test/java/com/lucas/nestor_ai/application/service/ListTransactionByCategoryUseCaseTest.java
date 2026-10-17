
package com.lucas.nestor_ai.application.service;

import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.TransactionId;
import com.lucas.nestor_ai.domain.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTransactionByCategoryUseCaseTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private ListTransactionByCategoryUseCase useCase;

    @Test
    void deveRetornarTransacoesDaCategoriaInformada() {
        var transaction = new Transaction(
                new TransactionId(),
                "Compra no mercado",
                new BigDecimal("125.50"),
                Category.GROCERIES
        );

        when(repository.findAllByCategory(Category.GROCERIES))
                .thenReturn(List.of(transaction));

        List<TransactionOutput> result =
                useCase.execute(Category.GROCERIES);

        assertEquals(1, result.size());
        assertEquals("Compra no mercado", result.get(0).description());
        assertEquals(new BigDecimal("125.50"), result.get(0).amount());
        assertEquals("GROCERIES", result.get(0).category());

        verify(repository).findAllByCategory(Category.GROCERIES);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremTransacoes() {
        when(repository.findAllByCategory(Category.AUTO))
                .thenReturn(List.of());

        List<TransactionOutput> result = useCase.execute(Category.AUTO);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByCategory(Category.AUTO);
    }
}
