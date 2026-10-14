package com.just.AiAutomationTranslater.translatorservice.vocabulary.repository;

import com.just.AiAutomationTranslater.translatorservice.vocabulary.entity.Term;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository extends JpaRepository<Term,Long> {

    @EntityGraph(attributePaths = {"definitions", "examples"})
    List<Term> findAllByUserId(Long userId);

    Optional<Term> findByIdAndUserId(Long id,Long userId);
    Optional<Term> findByWordTermAndUserId(String term, Long userId);

    boolean existsByWordTermAndUserId(String wordTerm, Long userId);
}
