package com.just.AiAutomationTranslater.translatorservice.translation.ai;

import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;

public interface TranslationAiClient {
    AiResponse translate(TranslateRequest translateRequest);
}
