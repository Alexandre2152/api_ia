package com.projetoestudo.ia.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;

public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.create(ollamaChatModel);
    }

}
