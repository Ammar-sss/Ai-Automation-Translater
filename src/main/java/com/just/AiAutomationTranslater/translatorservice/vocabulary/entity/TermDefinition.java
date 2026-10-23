package com.just.AiAutomationTranslater.translatorservice.vocabulary.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "term_definitions")
public class TermDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String wordDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id",nullable = false)
    private Term term;
}
