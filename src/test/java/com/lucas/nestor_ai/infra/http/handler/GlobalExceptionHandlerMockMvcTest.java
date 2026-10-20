package com.lucas.nestor_ai.infra.http.handler;

import com.lucas.nestor_ai.application.exception.AudioProcessingException;
import com.lucas.nestor_ai.application.exception.ConflictException;
import com.lucas.nestor_ai.application.exception.InvalidAudioException;
import com.lucas.nestor_ai.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerMockMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ExceptionProbeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void invalidAudioShouldReturn400() throws Exception {
        mockMvc.perform(get("/test-errors/invalid-audio"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("O arquivo de áudio não pode estar vazio."))
                .andExpect(jsonPath("$.path")
                        .value("/test-errors/invalid-audio"));
    }

    @Test
    void audioProcessingShouldReturn502() throws Exception {
        mockMvc.perform(get("/test-errors/audio-processing"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message")
                        .value("Não foi possível gerar uma resposta com a IA."))
                .andExpect(jsonPath("$.path")
                        .value("/test-errors/audio-processing"));
    }

    @Test
    void notFoundShouldReturn404() throws Exception {
        mockMvc.perform(get("/test-errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Transação não encontrada."));
    }

    @Test
    void conflictShouldReturn409() throws Exception {
        mockMvc.perform(get("/test-errors/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("Conflito de dados."));
    }

    @Test
    void illegalArgumentShouldReturn400() throws Exception {
        mockMvc.perform(get("/test-errors/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Um ou mais parâmetros informados são inválidos."));
    }

    @Test
    void unexpectedExceptionShouldReturn500() throws Exception {
        mockMvc.perform(get("/test-errors/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message")
                        .value("Ocorreu um erro interno. Tente novamente mais tarde."));
    }

    @RestController
    static class ExceptionProbeController {

        @GetMapping("/test-errors/invalid-audio")
        ResponseEntity<Void> invalidAudio() {
            throw new InvalidAudioException("O arquivo de áudio não pode estar vazio.");
        }

        @GetMapping("/test-errors/audio-processing")
        ResponseEntity<Void> audioProcessing() {
            throw new AudioProcessingException("Não foi possível gerar uma resposta com a IA.");
        }

        @GetMapping("/test-errors/not-found")
        ResponseEntity<Void> notFound() {
            throw new ResourceNotFoundException("Transação não encontrada.");
        }

        @GetMapping("/test-errors/conflict")
        ResponseEntity<Void> conflict() {
            throw new ConflictException("Conflito de dados.");
        }

        @GetMapping("/test-errors/illegal-argument")
        ResponseEntity<Void> illegalArgument() {
            throw new IllegalArgumentException("Argumento inválido.");
        }

        @GetMapping("/test-errors/unexpected")
        ResponseEntity<Void> unexpected() {
            throw new RuntimeException("Erro interno simulado.");
        }
    }
}