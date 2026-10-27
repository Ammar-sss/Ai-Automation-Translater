package com.just.AiAutomationTranslater.translatorservice.translation.service.impl;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.client.N8nClient;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;
import com.just.AiAutomationTranslater.translatorservice.translation.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationServiceImpl implements TranslationService{

    private final N8nClient n8nClient;

    @Override
    public Response<AiResponse> translate(TranslateRequest request) {

        String requestedTerm = request.getTermWord().trim().toLowerCase();

        TranslateRequest aiRequest = new TranslateRequest();
        aiRequest.setTermWord(requestedTerm);
        aiRequest.setSourceLanguage(request.getSourceLanguage());
        aiRequest.setTargetLanguage(request.getTargetLanguage());

        AiResponse aiResponse = n8nClient.translate(aiRequest);

        return Response.<AiResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Term Translated successfully")
                .data(aiResponse)
                .build();
    }
}
