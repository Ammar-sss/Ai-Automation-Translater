package com.just.AiAutomationTranslater.translatorservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClient) {
        return chatClient
                .defaultSystem("You are a precise translation and dictionary assistant. Respond only with the requested structure.")
                .build();
    }
}
