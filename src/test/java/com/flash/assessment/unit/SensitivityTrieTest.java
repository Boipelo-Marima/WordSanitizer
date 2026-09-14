package com.flash.assessment.unit;

import com.flash.assessment.component.SensitivityTrie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensitivityTrieTest {

    private SensitivityTrie trie;

    @BeforeEach
    void setUp() {
        trie = new SensitivityTrie();
        trie.insert("password");
        trie.insert("admin");
        trie.insert("secret key");
    }

    @Test
    void sanitize_shouldMaskExactMatch() {
        String result = trie.sanitize("my password is here", "*");
        assertEquals("my ******** is here", result);
    }

    @Test
    void sanitize_shouldHandleCaseInsensitivity() {
        String result = trie.sanitize("My PASSWORD is here", "*");
        assertEquals("My ******** is here", result);
    }

    @Test
    void sanitize_shouldMaskMultipleOccurrences() {
        String result = trie.sanitize("password and password", "*");
        assertEquals("******** and ********", result);
    }

    @Test
    void sanitize_shouldHandleMultiWordPhrases() {
        String result = trie.sanitize("this is a secret key test", "*");
        assertEquals("this is a ********** test", result);
    }

    @Test
    void sanitize_shouldReturnOriginalWhenNoMatch() {
        String result = trie.sanitize("hello world", "*");
        assertEquals("hello world", result);
    }

    @Test
    void sanitize_shouldHandleNullOrEmpty() {
        assertNull(trie.sanitize(null, "*"));
        assertEquals("", trie.sanitize("", "*"));
    }
}