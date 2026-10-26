package com.just.AiAutomationTranslater.translatorservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum LanguageTarget {

    ARABIC,
    ENGLISH,
    FRENCH,
    GERMANY,
    ITALIAN,
    SPANISH,
    RUSSIAN,
    KURDISH_SORANI,
    KURDISH_KURMANJI,
    DUTCH;


    @JsonCreator
    public static LanguageTarget from(String target) {
        if (target == null) {
            return null;
        }
        return LanguageTarget.valueOf(target.toUpperCase(Locale.ROOT));
    }
}
