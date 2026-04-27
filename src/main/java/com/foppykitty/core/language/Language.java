package com.foppykitty.core.language;

import java.util.ArrayList;

public class Language {
    private final String languageName;
    private final ArrayList<String> languageExtensions;
    private final LanguageLexer lexer;

    public Language(String languageName, ArrayList<String> languageExtensions, LanguageLexer lexer) {
        this.languageName = languageName;
        this.languageExtensions = languageExtensions;
        this.lexer = lexer;
    }

    public String getLanguageName() {
        return languageName;
    }

    public ArrayList<String> getLanguageExtensions() {
        return languageExtensions;
    }

    public LanguageLexer getLexer() {
        return lexer;
    }
}
