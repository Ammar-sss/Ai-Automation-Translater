package com.just.AiAutomationTranslater.translatorservice.translation.dto;

import com.just.AiAutomationTranslater.translatorservice.enums.LanguageTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiRequest {

    @NotBlank(message = "term is required")
    private String termWord;

    @NotNull(message = "Language target is required")
    private LanguageTarget targetLang;
}
