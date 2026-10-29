package com.just.AiAutomationTranslater.translatorservice.translation.ai;

import com.just.AiAutomationTranslater.translatorservice.exceptions.TranslationAiException;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringAiTranslationClient implements TranslationAiClient {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/translate-term.st")
    private Resource promptTemplate;

    @Override
    public AiResponse translate(TranslateRequest r) {
        try {
            AiResponse response = chatClient.prompt()
                    .user(u -> u.text(promptTemplate)
                            .param("term", r.getTermWord())
                            .param("source", r.getSourceLanguage().name())
                            .param("target", r.getTargetLanguage().name()))
                    .call()
                    .entity(AiResponse.class);

            if (response == null || response.getTermDefinitions() == null
                    || response.getTermDefinitions().isEmpty()) {
                throw new TranslationAiException("AI returned an empty or invalid response");
            }
            return response;

        } catch (TranslationAiException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI translation failed ({} -> {})",
                    r.getSourceLanguage(), r.getTargetLanguage(), e);
            throw new TranslationAiException("Translation temporarily unavailable");
        }
    }
}