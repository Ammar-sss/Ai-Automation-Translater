package com.just.AiAutomationTranslater.translatorservice.translation.dto;

import com.just.AiAutomationTranslater.translatorservice.enums.LanguageTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/*
Flutter to spring
**/
@Data
public class TranslateRequest {

    @NotBlank(message = "term is required")
    @Size(min = 1)
    private String termWord;

    @NotNull(message = "source language is required")
    private LanguageTarget sourceLanguage;

    @NotNull(message = "target language is required")
    private LanguageTarget targetLanguage;
}
