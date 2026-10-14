package com.just.AiAutomationTranslater.translatorservice.vocabulary.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TermDTO {

    private Long id;
    private String termWord;
    private Set<DefinitionDTO> definitions = new HashSet<>();
    private List<ExampleDTO> examples = new ArrayList<>();
}
