package com.just.AiAutomationTranslater.translatorservice.vocabulary.service.impl;

import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import com.just.AiAutomationTranslater.translatorservice.auth_user.service.UserService;
import com.just.AiAutomationTranslater.translatorservice.exceptions.ConflictException;
import com.just.AiAutomationTranslater.translatorservice.exceptions.NotFoundException;
import com.just.AiAutomationTranslater.translatorservice.res.Response;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.dto.TermDTO;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Example;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.ExampleDefinition;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Term;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.TermDefinition;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.repository.TermRepository;
import com.just.AiAutomationTranslater.translatorservice.vocabulary.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VocabularyServiceImpl implements  VocabularyService {

    private final TermRepository termRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
//  private final UserRepository userRepository;


    @Override
    public Response<TermDTO> getTermById(Long id) {
        User user = userService.getCurrentUser();

        Term term = termRepository.findByIdAndUserId(id,user.getId())
                .orElseThrow(() -> new NotFoundException("Term not found"));

        TermDTO termDTO = modelMapper.map(term, TermDTO.class);

        return Response.<TermDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Terms retrieved successfully by id")
                .data(termDTO)
                .build();
    }

    @Override
    public Response<List<TermDTO>> getAllTerms() {

        User user = userService.getCurrentUser();

        List<Term> terms = termRepository.findAllByUserId(user.getId());

        List<TermDTO> termDTOS = terms.stream()
                .map(term -> modelMapper.map(term, TermDTO.class))
                .toList();

        return Response.<List<TermDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All Terms retrieved successfully")
                .data(termDTOS)
                .build();
    }

    @Override
    public Response<TermDTO> updateTerm(TermDTO termDTO) {
        return null;
    }

    @Override
    @Transactional
    public Response<Void> deleteTermById(Long id) {
        User user = userService.getCurrentUser();

        Term term = termRepository.findByIdAndUserId(id,user.getId())
                .orElseThrow(() -> new NotFoundException("Term not found"));

        termRepository.delete(term);

        return Response.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Term deleted successfully")
                .build();
    }

    @Override
    public Response<TermDTO> findByTermWord(String termWord) {
        User user = userService.getCurrentUser();

        Term term = termRepository.findByTermWordAndUserId(termWord, user.getId())
                .orElseThrow(() -> new NotFoundException("Term not found"));
        TermDTO termDTO = modelMapper.map(term, TermDTO.class);

        return Response.<TermDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Terms retrieved successfully by id")
                .data(termDTO)
                .build();
    }

    @Override
    @Transactional
    public Response<TermDTO> confirmTermSave(AiResponse aiResponse) {
        User user = userService.getCurrentUser();
        if (termRepository.existsByTermWordAndUserId(aiResponse.getTermWord(), user.getId())) {
            throw new ConflictException("Term already exists");
        }

        Term term = Term.builder()
                .termWord(aiResponse.getTermWord())
                .user(user)
                .build();

        if (aiResponse.getTermDefinitions() != null) {
            for (String defi : aiResponse.getTermDefinitions()){
                TermDefinition termDefinition = new TermDefinition();
                termDefinition.setWordDefinition(defi);
                term.addTermDefinition(termDefinition);
            }
        }

        if (aiResponse.getExampleSentence() != null) {
            Example example =  new Example();
            example.setExampleSentence(aiResponse.getExampleSentence());
            term.addTermExample(example);
        }

        if (aiResponse.getExampleDefinition() != null) {
            ExampleDefinition exampleDefinition = new ExampleDefinition();
            exampleDefinition.setExampleDefinition(aiResponse.getExampleDefinition());
            term.addExampleDefinition(exampleDefinition);
        }

        termRepository.save(term);

        return Response.<TermDTO>builder()
                .statusCode(HttpStatus.OK.value()).
                message("Term saved successfully")
                .build();
    }

    @Override
    public Optional<TermDTO> findByNormalizedTerm(String normalizationTerm) {
        User user = userService.getCurrentUser();

        return termRepository.findByTermWordAndUserId(normalizationTerm,user.getId()).map(term -> modelMapper.map(term, TermDTO.class));
    }

}