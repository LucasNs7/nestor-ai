package com.lucas.nestor_ai.infra.http.controller;

import com.lucas.nestor_ai.application.service.ListTransactionByCategoryUseCase;
import com.lucas.nestor_ai.application.service.SaveTransactionUseCase;
import com.lucas.nestor_ai.domain.Category;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import com.lucas.nestor_ai.infra.http.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final ListTransactionByCategoryUseCase listByCategoryUseCase;
    private final TranscriptionModel transcriptionModel;
    private final SaveTransactionUseCase saveUseCase;
    private final TextToSpeechModel textToSpeechModel;
    private final ChatClient chatClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = saveUseCase.execute(request.toInput());

        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> findAllTransactionsByCategory(@PathVariable("category")
                                                                       String stringCategory) {
        var category = Category.valueOf(stringCategory);

        return listByCategoryUseCase.execute(category).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        String userPrompt = transcriptionModel.transcribe(file.getResource());
        var result = chatClient.prompt().user(userPrompt).call().content();

        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio, "UTF-8");

        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename("audio.mp3").build().toString()
        ).body(resource);
    }
}
