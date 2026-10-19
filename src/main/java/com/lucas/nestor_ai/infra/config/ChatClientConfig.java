package com.lucas.nestor_ai.infra.config;

import org.springframework.ai.chat.client.ChatClient;
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
            @Value("classpath:prompt/systemPrompt.st") Resource systemPrompt
    ) throws IOException {

        return builder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }
}
