
package com.lucas.nestor_ai.infra.http.controller;

import com.lucas.nestor_ai.application.dto.input.TransactionInput;
import com.lucas.nestor_ai.application.dto.output.TransactionOutput;
import com.lucas.nestor_ai.application.service.ListTransactionByCategoryUseCase;
import com.lucas.nestor_ai.application.service.SaveTransactionUseCase;
import com.lucas.nestor_ai.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListTransactionByCategoryUseCase listByCategoryUseCase;

    @MockitoBean
    private SaveTransactionUseCase saveUseCase;

    @MockitoBean
    private TranscriptionModel transcriptionModel;

    @MockitoBean
    private TextToSpeechModel textToSpeechModel;

    @MockitoBean
    private ChatClient chatClient;

    @Test
    void deveCriarTransacaoERetornarStatus201() throws Exception {
        var output = new TransactionOutput(
                "123",
                "Compra no mercado",
                new BigDecimal("125.50"),
                "GROCERIES"
        );

        when(saveUseCase.execute(any(TransactionInput.class)))
                .thenReturn(output);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Compra no mercado",
                                  "amount": 125.50,
                                  "category": "GROCERIES"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.description")
                        .value("Compra no mercado"))
                .andExpect(jsonPath("$.amount").value(125.50))
                .andExpect(jsonPath("$.category").value("GROCERIES"));

        verify(saveUseCase).execute(any(TransactionInput.class));
    }

    @Test
    void deveConsultarTransacoesPorCategoria() throws Exception {
        var output = new TransactionOutput(
                "456",
                "Compra na farmácia",
                new BigDecimal("32.90"),
                "PHARMA"
        );

        when(listByCategoryUseCase.execute(Category.PHARMA))
                .thenReturn(List.of(output));

        mockMvc.perform(get("/transactions/PHARMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("456"))
                .andExpect(jsonPath("$[0].description")
                        .value("Compra na farmácia"))
                .andExpect(jsonPath("$[0].amount").value(32.90))
                .andExpect(jsonPath("$[0].category").value("PHARMA"));

        verify(listByCategoryUseCase).execute(Category.PHARMA);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverTransacoes()
            throws Exception {

        when(listByCategoryUseCase.execute(Category.AUTO))
                .thenReturn(List.of());

        mockMvc.perform(get("/transactions/AUTO"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(listByCategoryUseCase).execute(Category.AUTO);
    }
}
