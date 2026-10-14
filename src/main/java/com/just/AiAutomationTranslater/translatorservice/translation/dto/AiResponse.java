package com.just.AiAutomationTranslater.translatorservice.translation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiResponse {

    private String termWord;
    private String exampleSentence;
    private List<String> termDefinitions;
    private String exampleDefinition;
}
