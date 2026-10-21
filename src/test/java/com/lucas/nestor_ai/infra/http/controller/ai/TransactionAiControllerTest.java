
package com.lucas.nestor_ai.infra.http.controller.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucas.nestor_ai.application.dto.output.ai.VoiceTransactionPreviewOutput;
import com.lucas.nestor_ai.infra.http.helper.AudioValidatorHelper;
import com.lucas.nestor_ai.application.dto.output.transaction.TransactionOutput;
import com.lucas.nestor_ai.application.service.ai.PrepareVoiceTransactionUseCase;
import com.lucas.nestor_ai.application.service.transaction.SaveTransactionUseCase;
import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionAiController.class)
class TransactionAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AudioValidatorHelper audioValidatorHelper;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private PrepareVoiceTransactionUseCase prepareVoiceTransactionUseCase;

    @MockitoBean
    private SaveTransactionUseCase saveTransactionUseCase;

    @Test
    void deveRetornarPreviaDaTransacaoAoEnviarAudio() throws Exception {
        var audio = new MockMultipartFile(
                "file",
                "transacao.mp3",
                "audio/mpeg",
                new byte[]{1, 2, 3}
        );

        var preview = mock(VoiceTransactionPreviewOutput.class);

        when(prepareVoiceTransactionUseCase.execute(any()))
                .thenReturn(preview);

        mockMvc.perform(multipart("/ai/transactions/voice")
                        .file(audio))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ));

        verify(prepareVoiceTransactionUseCase).execute(any());
        verifyNoInteractions(saveTransactionUseCase);
    }

    @Test
    void deveSalvarTransacaoAposConfirmacao() throws Exception {
        var category = Category.values()[0];
        var type = TransactionType.values()[0];

        var request = new TransactionRequest(
                "Almoço",
                new BigDecimal("35.90"),
                category,
                type,
                LocalDate.of(2026, 10, 21)
        );

        var output = new TransactionOutput(
                "123",
                request.description(),
                request.amount(),
                category.name(),
                type.name(),
                request.date(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(saveTransactionUseCase.execute(any()))
                .thenReturn(output);

        mockMvc.perform(post("/ai/transactions/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Almoço"))
                .andExpect(jsonPath("$.amount").value(35.90))
                .andExpect(jsonPath("$.category").value(category.name()))
                .andExpect(jsonPath("$.type").value(type.name()));

        verify(saveTransactionUseCase).execute(any());
    }

    @Test
    void naoDeveSalvarTransacaoComDadosInvalidos() throws Exception {
        var invalidRequest = """
                {
                    "description": "",
                    "amount": 0,
                    "category": null,
                    "type": null,
                    "date": null
                }
                """;

        mockMvc.perform(post("/ai/transactions/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(saveTransactionUseCase);
    }
}
