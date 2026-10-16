package com.just.AiAutomationTranslater.translatorservice.translation.dto;

import jakarta.validation.constraints.NotBlank;
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
}
