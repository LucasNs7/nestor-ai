package com.lucas.nestor_ai.infra.http.controller.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucas.nestor_ai.application.dto.input.transaction.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
import com.lucas.nestor_ai.application.exception.ConflictException;
import com.lucas.nestor_ai.application.exception.ResourceNotFoundException;
import com.lucas.nestor_ai.application.service.transaction.*;
import com.lucas.nestor_ai.domain.Transaction;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListTransactionsByCategoryUseCase listByCategoryUseCase;

    @MockitoBean
    private ListTransactionsByPeriodUseCase listByPeriodUseCase;

    @MockitoBean
    private ListTransactionsByDateUseCase listByDateUseCase;

    @MockitoBean
    private ListTransactionsByTypeUseCase listByTypeUseCase;

    @MockitoBean
    private DeleteTransactionByIdUseCase deleteByIdUseCase;

    @MockitoBean
    private UpdateTransactionUseCase updateUseCase;

    @MockitoBean
    private SaveTransactionUseCase saveUseCase;

    private Category category;
    private TransactionType type;
    private LocalDate date;
    private Transaction transaction;
    private TransactionRequest validRequest;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }
    }

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

        validRequest = new TransactionRequest(
                "Compra de teste",
                new BigDecimal("50.00"),
                category,
                type,
                date
        );
    }

    @Test
    void createShouldReturn201AndTransaction() throws Exception {
        when(saveUseCase.execute(any(TransactionInput.class)))
                .thenReturn(TransactionOutput.from(transaction));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description")
                        .value("Compra de teste"))
                .andExpect(jsonPath("$.amount")
                        .value(50.00))
                .andExpect(jsonPath("$.date")
                        .value("2026-10-19"));

        verify(saveUseCase).execute(any(TransactionInput.class));
    }

    @Test
    void createShouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{json-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path")
                        .value("/transactions"));

        verifyNoInteractions(saveUseCase);
    }

    @Test
    void findByCategoryShouldReturn200AndList() throws Exception {
        when(listByCategoryUseCase.execute(category))
                .thenReturn(List.of(TransactionOutput.from(transaction)));

        mockMvc.perform(get("/transactions/category/{category}", category.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].description")
                        .value("Compra de teste"));

        verify(listByCategoryUseCase).execute(category);
    }

    @Test
    void findByTypeShouldReturn200AndList() throws Exception {
        when(listByTypeUseCase.execute(type))
                .thenReturn(List.of(TransactionOutput.from(transaction)));

        mockMvc.perform(get("/transactions/type/{type}", type.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(1));

        verify(listByTypeUseCase).execute(type);
    }

    @Test
    void findByDateShouldReturn200AndList() throws Exception {
        when(listByDateUseCase.execute(date))
                .thenReturn(List.of(TransactionOutput.from(transaction)));

        mockMvc.perform(get("/transactions/date/{date}", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date")
                        .value("2026-10-19"));

        verify(listByDateUseCase).execute(date);
    }

    @Test
    void findByDateShouldReturn400WhenDateIsInvalid() throws Exception {
        mockMvc.perform(get("/transactions/date/19-10-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.path")
                        .value("/transactions/date/19-10-2026"));

        verifyNoInteractions(listByDateUseCase);
    }

    @Test
    void findByPeriodShouldReturn200AndList() throws Exception {
        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 20);

        when(listByPeriodUseCase.execute(start, end))
                .thenReturn(List.of(TransactionOutput.from(transaction)));

        mockMvc.perform(get("/transactions")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(1));

        verify(listByPeriodUseCase).execute(start, end);
    }

    @Test
    void findByPeriodShouldReturn400WhenEndIsMissing() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("start", "2026-10-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("O parâmetro obrigatório end não foi informado."));

        verifyNoInteractions(listByPeriodUseCase);
    }

    @Test
    void findByPeriodShouldReturn400WhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/transactions")
                        .param("start", "01-10-2026")
                        .param("end", "20-10-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400));

        verifyNoInteractions(listByPeriodUseCase);
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/transactions/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteByIdUseCase).execute(id);
    }

    @Test
    void deleteShouldReturn400WhenIdIsNotUuid() throws Exception {
        mockMvc.perform(delete("/transactions/{id}", "id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400));

        verifyNoInteractions(deleteByIdUseCase);
    }

    @Test
    void findByCategoryShouldReturn400WhenEnumIsInvalid() throws Exception {
        mockMvc.perform(get("/transactions/category/INVALID_CATEGORY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.path")
                        .value("/transactions/category/INVALID_CATEGORY"));

        verifyNoInteractions(listByCategoryUseCase);
    }

    @Test
    void findByCategoryShouldReturn200WithEmptyList() throws Exception {
        when(listByCategoryUseCase.execute(category)).thenReturn(List.of());

        mockMvc.perform(get("/transactions/category/{category}", category.name()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn404WhenResourceIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(updateUseCase.execute(eq(id), any()))
                .thenThrow(new ResourceNotFoundException("Transação não encontrada."));

        var patchRequest = """
                {
                  "description": "Compra atualizada"
                }
                """;

        mockMvc.perform(patch("/transactions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Transação não encontrada."))
                .andExpect(jsonPath("$.path")
                        .value("/transactions/" + id));
    }

    @Test
    void shouldReturn409WhenConflictOccurs() throws Exception {
        when(saveUseCase.execute(any(TransactionInput.class)))
                .thenThrow(new ConflictException("Transação em conflito."));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.message")
                        .value("Transação em conflito."));
    }

    @Test
    void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {
        when(saveUseCase.execute(any(TransactionInput.class)))
                .thenThrow(new RuntimeException("Erro interno simulado."));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status")
                        .value(500))
                .andExpect(jsonPath("$.message")
                        .value("Ocorreu um erro interno. Tente novamente mais tarde."));
    }

    @Test
    void updateShouldReturn200AndUpdatedTransaction() throws Exception {
        UUID id = UUID.randomUUID();

        when(updateUseCase.execute(eq(id), any(TransactionInput.class)))
                .thenReturn(TransactionOutput.from(transaction));

        mockMvc.perform(patch("/transactions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "description": "Compra atualizada",
                              "amount": 75.00
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value("Compra de teste"))
                .andExpect(jsonPath("$.amount")
                        .value(50.00));

        verify(updateUseCase).execute(eq(id), any(TransactionInput.class));
    }

    @Test
    void createShouldReturn400WhenBeanValidationFails() throws Exception {
        var invalidRequest = new TransactionRequest(
                "",
                new BigDecimal("0.00"),
                category,
                type,
                date
        );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Os dados enviados são inválidos."))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()")
                        .value(org.hamcrest.Matchers.greaterThan(0)));

        verifyNoInteractions(saveUseCase);
    }
}