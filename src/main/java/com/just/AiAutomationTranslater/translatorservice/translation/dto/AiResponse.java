package com.just.AiAutomationTranslater.translatorservice.translation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {

    private String termWord;
    private String exampleSentence;
    private Set<String> termDefinitions;
    private String exampleDefinition;
}
