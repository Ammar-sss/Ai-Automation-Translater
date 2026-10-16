package com.just.AiAutomationTranslater.translatorservice.vocabulary.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class TermDTO {

    private Long id;
    private String termWord;
    private Set<TermDefinitionDTO> termDefinitions = new HashSet<>();
    private Set<ExampleDTO> termExamples = new HashSet<>();
    private Set<ExampleDefinitionDTO> exampleDefinitions = new HashSet<>();
}
