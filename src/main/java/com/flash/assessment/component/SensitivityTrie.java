package com.flash.assessment.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SensitivityTrie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    public String sanitize(String text, String replacementMask) {
        if (text == null || text.isEmpty()) return text;

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            TrieNode current = root;
            int j = i;
            int lastMatchEnd = -1;

            while (j < chars.length && current.children.containsKey(Character.toLowerCase(chars[j]))) {
                current = current.children.get(Character.toLowerCase(chars[j]));
                j++;
                if (current.isEndOfWord) {
                    lastMatchEnd = j;
                }
            }

            if (lastMatchEnd != -1) {
                // Replace matched sequence with the mask (e.g., "***")
                for (int k = i; k < lastMatchEnd; k++) {
                    chars[k] = replacementMask.charAt(0); // Simplified mask application
                }
                i = lastMatchEnd - 1;
            }
        }
        return new String(chars);
    }

    public void clear(){root.children.clear();}

    public boolean isPopulated(){return !root.children.isEmpty(); }
}