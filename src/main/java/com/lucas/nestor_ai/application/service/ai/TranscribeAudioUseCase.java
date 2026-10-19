package com.lucas.nestor_ai.application.service.ai;

import com.lucas.nestor_ai.application.exception.AudioProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranscribeAudioUseCase {

    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;
    private final ChatClient chatClient;

    public byte[] execute(Resource audioResource) {

        String transcribedText = transcribe(audioResource);
        String response = generateResponse(transcribedText);

        return generateAudio(response);
    }

    private String transcribe(Resource audioResource) {

        String text;

        try {
            text = transcriptionModel.transcribe(audioResource);
        } catch (RuntimeException exception) {
            throw new AudioProcessingException("Não foi possível processar o áudio enviado.", exception);
        }

        if (text == null || text.isBlank()) {
            throw new AudioProcessingException("Não foi possível identificar conteúdo no áudio.");
        }

        return text;
    }

    private String generateResponse(String transcribedText) {

        String response;

        try {
            response = chatClient.prompt().user(transcribedText).call().content();
        } catch (RuntimeException exception) {
            throw new AudioProcessingException("Não foi possível gerar uma resposta com a IA.", exception);
        }

        if (response == null || response.isBlank()) {
            throw new AudioProcessingException("A IA não retornou uma resposta válida.");
        }

        return response;
    }

    private byte[] generateAudio(String response) {

        byte[] audio;

        try {
            audio = textToSpeechModel.call(response);
        } catch (RuntimeException exception) {
            throw new AudioProcessingException("Não foi possível gerar o áudio da resposta.", exception);
        }

        if (audio == null || audio.length == 0) {
            throw new AudioProcessingException("O modelo não gerou conteúdo de áudio.");
        }

        return audio;
    }
}
