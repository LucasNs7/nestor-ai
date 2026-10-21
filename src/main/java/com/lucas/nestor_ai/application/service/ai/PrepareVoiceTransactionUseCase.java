package com.lucas.nestor_ai.application.service.ai;

import com.lucas.nestor_ai.application.dto.input.ai.VoiceTransactionFields;
import com.lucas.nestor_ai.application.dto.output.ai.VoiceTransactionPreviewOutput;
import com.lucas.nestor_ai.application.exception.AudioProcessingException;

import com.lucas.nestor_ai.domain.enums.Category;
import com.lucas.nestor_ai.domain.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrepareVoiceTransactionUseCase {

    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;
    private final ChatClient chatClient;

    public VoiceTransactionPreviewOutput execute(Resource audioResource) {
        String transcription = transcribe(audioResource);
        VoiceTransactionFields fields = interpret(transcription);

        List<String> missingFields = findMissingFields(fields);
        String message = buildMessage(fields, missingFields);
        String audioBase64 = synthesizeMessage(message);

        return new VoiceTransactionPreviewOutput(
                transcription,
                fields,
                missingFields,
                missingFields.isEmpty(),
                message,
                audioBase64
        );
    }

    private String transcribe(Resource audioResource) {
        try {
            String text = transcriptionModel.transcribe(audioResource);

            if (text == null || text.isBlank()) {
                throw new AudioProcessingException(
                        "Não foi possível identificar conteúdo no áudio."
                );
            }

            return text.trim();

        } catch (AudioProcessingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AudioProcessingException(
                    "Não foi possível transcrever o áudio enviado.",
                    exception
            );
        }
    }

    private VoiceTransactionFields interpret(String transcription) {
        String prompt = """
                Você é um assistente de registro financeiro.
                Extraia os dados de uma única transação a partir do texto informado.

                Retorne os campos estruturados solicitados.
                Não invente informações e não complete campos sem evidências.

                Regras:
                - description: descrição curta da transação, em português.
                - amount: valor positivo em reais, como número decimal.
                - category: escolha exatamente um valor do enum Category.
                - type: escolha exatamente um valor do enum TransactionType.
                - date: data no formato ISO yyyy-MM-dd.
                - Data atual: %s.
                - Interprete "hoje", "ontem" e "anteontem" em relação à data atual.
                - Se não conseguir identificar um campo com segurança, retorne null.
                - Não confunda o tipo da transação com sua categoria.
                - Categorias válidas: %s.
                - Tipos válidos: %s.

                Texto transcrito:
                %s
                """.formatted(
                LocalDate.now(),
                enumValues(Category.values()),
                enumValues(TransactionType.values()),
                transcription
        );

        try {
            VoiceTransactionFields fields = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(VoiceTransactionFields.class);

            if (fields == null) {
                throw new AudioProcessingException(
                        "A IA não conseguiu estruturar os dados da transação."
                );
            }

            return normalize(fields);

        } catch (AudioProcessingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AudioProcessingException(
                    "Não foi possível interpretar os dados da transação.",
                    exception
            );
        }
    }

    private VoiceTransactionFields normalize(VoiceTransactionFields fields) {
        String description = fields.description();

        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }
        }

        var amount = fields.amount();

        if (amount != null && amount.signum() <= 0) {
            amount = null;
        }

        return new VoiceTransactionFields(
                description,
                amount,
                fields.category(),
                fields.type(),
                fields.date()
        );
    }

    private List<String> findMissingFields(VoiceTransactionFields fields) {
        List<String> missing = new ArrayList<>();

        if (fields.description() == null) {
            missing.add("description");
        }

        if (fields.amount() == null) {
            missing.add("amount");
        }

        if (fields.category() == null) {
            missing.add("category");
        }

        if (fields.type() == null) {
            missing.add("type");
        }

        if (fields.date() == null) {
            missing.add("date");
        }

        return List.copyOf(missing);
    }

    private String buildMessage(
            VoiceTransactionFields fields,
            List<String> missingFields
    ) {
        if (!missingFields.isEmpty()) {
            String labels = missingFields.stream()
                    .map(this::fieldLabel)
                    .collect(Collectors.joining(", "));

            return "Não consegui identificar com segurança: "
                    + labels
                    + ". Informe os dados faltantes antes de confirmar. "
                    + "Nenhuma transação foi salva.";
        }

        String amount = NumberFormat
                .getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
                .format(fields.amount());

        String date = fields.date()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return "Confira a transação. Descrição: "
                + fields.description()
                + ". Valor: " + amount
                + ". Categoria: " + fields.category()
                + ". Tipo: " + fields.type()
                + ". Data: " + date
                + ". Confirme os dados para registrar a transação. "
                + "Ainda não salvei nenhuma informação.";
    }

    private String fieldLabel(String field) {
        return switch (field) {
            case "description" -> "a descrição";
            case "amount" -> "o valor";
            case "category" -> "a categoria";
            case "type" -> "o tipo da transação";
            case "date" -> "a data";
            default -> field;
        };
    }

    private String synthesizeMessage(String message) {
        try {
            byte[] audio = textToSpeechModel.call(message);

            if (audio == null || audio.length == 0) {
                throw new AudioProcessingException(
                        "O modelo não gerou o áudio da confirmação."
                );
            }

            return Base64.getEncoder().encodeToString(audio);

        } catch (AudioProcessingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AudioProcessingException(
                    "Não foi possível gerar o áudio da confirmação.",
                    exception
            );
        }
    }

    private String enumValues(Enum<?>[] values) {
        return Arrays.stream(values)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}