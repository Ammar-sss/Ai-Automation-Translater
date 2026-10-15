package com.just.AiAutomationTranslater.translatorservice.vocabulary.controller;

import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.dto.TermDTO;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.service.VocabularyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocabulary")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping("/{id}")
    public ResponseEntity<Response<TermDTO>> getTermById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(vocabularyService.getTermById(id));
    }

    @GetMapping
    public ResponseEntity<Response<List<TermDTO>>> getAllTerms() {
        return ResponseEntity.ok(vocabularyService.getAllTerms());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteTermById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(vocabularyService.deleteTermById(id));
    }


}

