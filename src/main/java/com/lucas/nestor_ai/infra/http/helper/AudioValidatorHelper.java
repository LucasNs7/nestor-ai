package com.lucas.nestor_ai.infra.http.helper;

import com.lucas.nestor_ai.application.exception.InvalidAudioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class AudioValidatorHelper {

    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidAudioException("O arquivo de áudio não pode estar vazio.");
        }

        if (file.getSize() > MAX_AUDIO_SIZE) {
            throw new InvalidAudioException("O áudio deve ter no máximo 10 MB.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.toLowerCase(java.util.Locale.ROOT).startsWith("audio/")) {
            throw new InvalidAudioException("O arquivo enviado deve ser um áudio.");
        }
    }
}
