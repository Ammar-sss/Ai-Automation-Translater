package com.just.AiAutomationTranslater.translatorservice.translation.controller;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;
import com.just.AiAutomationTranslater.translatorservice.translation.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/translate")
public class TranslateController {

    private final TranslationService translationService;

    @PostMapping
    ResponseEntity<Response<AiResponse>> translate(@RequestBody TranslateRequest request){
        return ResponseEntity.ok(translationService.translate(request));
    }
}
