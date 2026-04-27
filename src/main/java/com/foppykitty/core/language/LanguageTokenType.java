package com.foppykitty.core.language;

public interface LanguageTokenType {
    String getTokenType();
    default String getDescription() {
        return "No description provided by extension developer...";
    }
}
