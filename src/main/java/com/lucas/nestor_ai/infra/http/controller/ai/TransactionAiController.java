package com.lucas.nestor_ai.infra.http.controller.ai;

import com.lucas.nestor_ai.application.service.ai.PrepareVoiceTransactionUseCase;
import com.lucas.nestor_ai.application.service.transaction.SaveTransactionUseCase;
import com.lucas.nestor_ai.infra.http.dto.request.TransactionRequest;
import com.lucas.nestor_ai.infra.http.dto.response.ai.VoiceTransactionPreviewResponse;
import com.lucas.nestor_ai.infra.http.dto.response.transaction.TransactionResponse;
import com.lucas.nestor_ai.infra.http.helper.AudioValidatorHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/transactions")
public class TransactionAiController {

    private final PrepareVoiceTransactionUseCase prepareVoiceUseCase;
    private final SaveTransactionUseCase saveTransactionUseCase;
    private final AudioValidatorHelper audioValidatorHelper;

    @PostMapping(
            value = "/voice",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public VoiceTransactionPreviewResponse prepareFromVoice(@RequestParam("file") MultipartFile file) {
        audioValidatorHelper.validate(file);

        return VoiceTransactionPreviewResponse.from(prepareVoiceUseCase.execute(file.getResource()));
    }

    @PostMapping(
            value = "/confirm",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse confirmAndSave(@Valid @RequestBody TransactionRequest request) {
        var saved = saveTransactionUseCase.execute(request.toInput());

        return TransactionResponse.from(saved);
    }
}