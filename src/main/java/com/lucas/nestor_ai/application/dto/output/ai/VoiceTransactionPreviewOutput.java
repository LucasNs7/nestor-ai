package com.lucas.nestor_ai.application.dto.output.ai;

import com.lucas.nestor_ai.application.dto.input.ai.VoiceTransactionFields;

import java.util.List;

public record VoiceTransactionPreviewOutput(
        String transcribedText,
        VoiceTransactionFields transaction,
        List<String> missingFields,
        boolean readyForConfirmation,
        String message,
        String audioBase64
) {}
