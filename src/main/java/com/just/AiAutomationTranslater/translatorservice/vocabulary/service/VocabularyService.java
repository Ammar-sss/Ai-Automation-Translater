package com.just.AiAutomationTranslater.translatorservice.vocabulary.service;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.dto.TermDTO;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Term;

import java.util.List;
import java.util.Optional;

public interface VocabularyService {

    Response<TermDTO> getTermById(Long id);
    Response<List<TermDTO>> getAllTerms();
    Response<TermDTO> updateTerm(TermDTO termDTO);
    Response<Void>  deleteTermById(Long id);
    Response<TermDTO> findByTermWord(String termWord);
    Response<TermDTO> confirmTermSave(AiResponse aiResponse);

    Optional<TermDTO> findByNormalizedTerm(String normalizationTerm);
}
