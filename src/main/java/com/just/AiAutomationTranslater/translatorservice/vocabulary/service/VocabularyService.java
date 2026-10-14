package com.just.AiAutomationTranslater.translatorservice.vocabulary.service;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.dto.TermDTO;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Term;

import java.util.List;
import java.util.Optional;

public interface VocabularyService {

    Response<TermDTO> getTermById(Long id);
    Response<List<TermDTO>> getAllTerms();
    Response<TermDTO> updateTerm(TermDTO termDTO);
    Response<Void>  deleteTermById(Long id);

    Optional<TermDTO> findByNormalizedTerm(String normalizationTerm);
    Term saveTerm(Term term);

}
