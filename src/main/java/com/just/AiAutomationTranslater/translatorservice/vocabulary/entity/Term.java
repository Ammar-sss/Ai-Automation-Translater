package com.just.AiAutomationTranslater.translatorservice.vocabulary.entity;

import com.just.AiAutomationTranslater.translatorservice.auth_user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "terms",
        uniqueConstraints = @UniqueConstraint(name = "uc_user_term", columnNames = {"user_id", "word_term"})
)
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String termWord;

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TermDefinition> termDefinitions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Example> termExamples = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExampleDefinition> exampleDefinitions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void addTermDefinition(TermDefinition d) {
        termDefinitions.add(d);
        d.setTerm(this);
    }

    public void removeTermDefinition(TermDefinition d) {
        termDefinitions.remove(d);
        d.setTerm(null);
    }

    public void addTermExample(Example e) {
        termExamples.add(e);
        e.setTerm(this);
    }

    public void removeTermExample(Example e) {
        termExamples.remove(e);
        e.setTerm(null);
    }

    public void addExampleDefinition(ExampleDefinition d) {
        exampleDefinitions.add(d);
        d.setTerm(this);
    }

    public void removeExampleDefinition(ExampleDefinition d) {
        exampleDefinitions.remove(d);
        d.setTerm(null);
    }
}
