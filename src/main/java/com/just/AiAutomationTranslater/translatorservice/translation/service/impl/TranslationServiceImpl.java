package com.just.AiAutomationTranslater.translatorservice.translation.service.impl;

import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.UserService;
import com.just.AiAutomationTranslater.translatorservice.enums.LanguageTarget;
import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.client.N8nClient;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiRequest;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;
import com.just.AiAutomationTranslater.translatorservice.translation.service.TranslationService;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Example;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.ExampleDefinition;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Term;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.TermDefinition;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.repository.TermRepository;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationServiceImpl implements TranslationService{

    private final N8nClient n8nClient;
    private final TermRepository termRepository;
    private final UserService userService;
    private final VocabularyService vocabularyService;

    @Override
    public Response<AiResponse> translate(TranslateRequest request) {

        User user = userService.getCurrentUser();
        String requestedTerm = request.getTermWord().trim().toLowerCase();

        if(termRepository.findByTermWordAndUserId(requestedTerm,user.getId()).isPresent()){
            throw new ConflictException("Term already exists");
        }

        AiRequest aiRequest = new AiRequest();
        aiRequest.setTermWord(requestedTerm);
        aiRequest.setTargetLang(LanguageTarget.AR);

        AiResponse aiResponse = n8nClient.translate(aiRequest);

        Term term = Term.builder()
                .termWord(requestedTerm)
                .build();

        if (aiResponse.getTermDefinitions() != null) {
            for (String defString : aiResponse.getTermDefinitions()) {
                TermDefinition definition = new TermDefinition();
                definition.setWordDefinition(defString);
                term.addTermDefinition(definition);
            }
        }

        if (aiResponse.getExampleSentence() != null) {
            Example exampleDefinition = new Example();
            exampleDefinition.setExampleSentence(aiResponse.getExampleSentence());

            term.addTermExample(exampleDefinition);
        }

        if (aiResponse.getExampleDefinition() != null) {
            ExampleDefinition exampleDefinition = new ExampleDefinition();
            exampleDefinition.setExampleDefinition(aiResponse.getExampleDefinition());
            term.addExampleDefinition(exampleDefinition);
        }

        vocabularyService.saveTerm(term);

        return Response.<AiResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Term Translated successfully")
                .data(aiResponse)
                .build();
    }
}
