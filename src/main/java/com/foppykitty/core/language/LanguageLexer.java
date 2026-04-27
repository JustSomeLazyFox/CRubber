package com.foppykitty.core.language;

import java.util.ArrayList;

public interface LanguageLexer {
    LanguageTokens getLanguageTokens();
    ArrayList<LanguageToken> tokenize(String text);
}
