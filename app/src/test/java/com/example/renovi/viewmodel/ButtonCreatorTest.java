package com.example.renovi.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ButtonCreatorTest {
    @Test
    void normalizeString_shouldReplaceSpecialCharacters() {
        ButtonCreator buttonCreator = new ButtonCreator(null); // null-Context, da die Methode keinen Kontext verwendet
        String result = buttonCreator.normalizeString("äöüÄÖÜß");
        assertEquals("aeoeueAeOeUess", result);
    }

    @Test
    void normalizeString_shouldKeepNonSpecialCharacters() {
        ButtonCreator buttonCreator = new ButtonCreator(null);
        String input = "Test123";
        String result = buttonCreator.normalizeString(input);
        assertEquals(input, result);
    }

    @Test
    void normalizeString_shouldHandleEmptyString() {
        ButtonCreator buttonCreator = new ButtonCreator(null);
        String result = buttonCreator.normalizeString("");
        assertEquals("", result);
    }
}
