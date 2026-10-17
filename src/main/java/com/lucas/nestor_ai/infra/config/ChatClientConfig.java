package com.lucas.nestor_ai.infra.config;

import com.lucas.nestor_ai.application.service.ListTransactionByCategoryUseCase;
import com.lucas.nestor_ai.application.service.SaveTransactionUseCase;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.Charset;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            @Value("classpath:prompt/systemPrompt.st") Resource systemPrompt,
            SaveTransactionUseCase saveUseCaseClass,
            ListTransactionByCategoryUseCase listByCategoryUseCaseClass
    ) throws IOException {

        var toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(saveUseCaseClass, listByCategoryUseCaseClass)
                .build();

        return builder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(toolCallbacks)
                .build();
    }
}
