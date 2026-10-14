package com.just.AiAutomationTranslater.translatorservice.translation.service;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;

public interface TranslationService {

    Response<AiResponse> translate(TranslateRequest request);
}
