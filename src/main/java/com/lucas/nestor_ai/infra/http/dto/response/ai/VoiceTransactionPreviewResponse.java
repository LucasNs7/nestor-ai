package com.lucas.nestor_ai.infra.http.dto.response.ai;

import com.lucas.nestor_ai.application.dto.input.ai.VoiceTransactionFields;
import com.lucas.nestor_ai.application.dto.output.ai.VoiceTransactionPreviewOutput;

import java.util.List;

public record VoiceTransactionPreviewResponse(
        String transcribedText,
        VoiceTransactionFields transaction,
        List<String> missingFields,
        boolean readyForConfirmation,
        String message,
        String audioBase64
) {

    public static VoiceTransactionPreviewResponse from(
            VoiceTransactionPreviewOutput output
    ) {
        return new VoiceTransactionPreviewResponse(
                output.transcribedText(),
                output.transaction(),
                output.missingFields(),
                output.readyForConfirmation(),
                output.message(),
                output.audioBase64()
        );
    }
}
