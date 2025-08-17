package com.anggastudio.printama.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link StrUtil}
 * Tests string utility methods including non-ASCII character encoding
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class StrUtilTest {

    // Test encodeNonAscii method
    @Test
    public void testEncodeNonAscii_basicCharacters() {
        String input = "Hello World";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Basic ASCII characters should remain unchanged", "Hello World", result);
    }

    @Test
    public void testEncodeNonAscii_emptyString() {
        String input = "";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Empty string should remain empty", "", result);
    }

    @Test
    public void testEncodeNonAscii_nullInput() {
        String result = StrUtil.encodeNonAscii(null);
        assertNull("Null input should return null", result);
    }

    @Test
    public void testEncodeNonAscii_accentedCharacters() {
        // Test various accented characters
        assertEquals("à should be converted to a", "a", StrUtil.encodeNonAscii("à"));
        assertEquals("á should be converted to a", "a", StrUtil.encodeNonAscii("á"));
        assertEquals("â should be converted to a", "a", StrUtil.encodeNonAscii("â"));
        assertEquals("ã should be converted to a", "a", StrUtil.encodeNonAscii("ã"));
        assertEquals("ä should be converted to a", "a", StrUtil.encodeNonAscii("ä"));
        assertEquals("å should be converted to a", "a", StrUtil.encodeNonAscii("å"));
        assertEquals("æ should be converted to ae", "ae", StrUtil.encodeNonAscii("æ"));
    }

    @Test
    public void testEncodeNonAscii_eCharacters() {
        assertEquals("è should be converted to e", "e", StrUtil.encodeNonAscii("è"));
        assertEquals("é should be converted to e", "e", StrUtil.encodeNonAscii("é"));
        assertEquals("ê should be converted to e", "e", StrUtil.encodeNonAscii("ê"));
        assertEquals("ë should be converted to e", "e", StrUtil.encodeNonAscii("ë"));
    }

    @Test
    public void testEncodeNonAscii_iCharacters() {
        assertEquals("ì should be converted to i", "i", StrUtil.encodeNonAscii("ì"));
        assertEquals("í should be converted to i", "i", StrUtil.encodeNonAscii("í"));
        assertEquals("î should be converted to i", "i", StrUtil.encodeNonAscii("î"));
        assertEquals("ï should be converted to i", "i", StrUtil.encodeNonAscii("ï"));
    }

    @Test
    public void testEncodeNonAscii_oCharacters() {
        assertEquals("ò should be converted to o", "o", StrUtil.encodeNonAscii("ò"));
        assertEquals("ó should be converted to o", "o", StrUtil.encodeNonAscii("ó"));
        assertEquals("ô should be converted to o", "o", StrUtil.encodeNonAscii("ô"));
        assertEquals("õ should be converted to o", "o", StrUtil.encodeNonAscii("õ"));
        assertEquals("ö should be converted to o", "o", StrUtil.encodeNonAscii("ö"));
        assertEquals("ø should be converted to o", "o", StrUtil.encodeNonAscii("ø"));
        assertEquals("œ should be converted to oe", "oe", StrUtil.encodeNonAscii("œ"));
    }

    @Test
    public void testEncodeNonAscii_uCharacters() {
        assertEquals("ù should be converted to u", "u", StrUtil.encodeNonAscii("ù"));
        assertEquals("ú should be converted to u", "u", StrUtil.encodeNonAscii("ú"));
        assertEquals("û should be converted to u", "u", StrUtil.encodeNonAscii("û"));
        assertEquals("ü should be converted to u", "u", StrUtil.encodeNonAscii("ü"));
    }

    @Test
    public void testEncodeNonAscii_specialCharacters() {
        assertEquals("ç should be converted to c", "c", StrUtil.encodeNonAscii("ç"));
        assertEquals("ñ should be converted to n", "n", StrUtil.encodeNonAscii("ñ"));
        assertEquals("ÿ should be converted to y", "y", StrUtil.encodeNonAscii("ÿ"));
        assertEquals("ß should be converted to ss", "ss", StrUtil.encodeNonAscii("ß"));
    }

    @Test
    public void testEncodeNonAscii_uppercaseCharacters() {
        assertEquals("À should be converted to A", "A", StrUtil.encodeNonAscii("À"));
        assertEquals("Á should be converted to A", "A", StrUtil.encodeNonAscii("Á"));
        assertEquals("È should be converted to E", "E", StrUtil.encodeNonAscii("È"));
        assertEquals("É should be converted to E", "E", StrUtil.encodeNonAscii("É"));
        assertEquals("Ñ should be converted to N", "N", StrUtil.encodeNonAscii("Ñ"));
        assertEquals("Ç should be converted to C", "C", StrUtil.encodeNonAscii("Ç"));
    }

    @Test
    public void testEncodeNonAscii_mixedString() {
        String input = "Café résumé naïve";
        String expected = "Cafe resume naive";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Mixed string with accented characters", expected, result);
    }

    @Test
    public void testEncodeNonAscii_sentenceWithAccents() {
        String input = "Hôtel Müller à Zürich";
        String expected = "Hotel Muller a Zurich";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Sentence with various accented characters", expected, result);
    }

    @Test
    public void testEncodeNonAscii_numbersAndSymbols() {
        String input = "123!@#$%^&*()";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Numbers and symbols should remain unchanged", "123!@#$%^&*()", result);
    }

    @Test
    public void testEncodeNonAscii_whitespacePreservation() {
        String input = "  café   résumé  ";
        String expected = "  cafe   resume  ";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Whitespace should be preserved", expected, result);
    }

    @Test
    public void testEncodeNonAscii_newlinesAndTabs() {
        String input = "café\nrésumé\tnaïve";
        String expected = "cafe\nresume\tnaive";
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Newlines and tabs should be preserved", expected, result);
    }

    @Test
    public void testEncodeNonAscii_repeatedCharacters() {
        String input = "aaaàààeeeéééoooóóó";
        String expected = "aaaaaaeeeeeeoooooo"; // Fixed: should be 6 o's, not 7
        String result = StrUtil.encodeNonAscii(input);
        assertEquals("Repeated characters should all be converted", expected, result);
    }

    @Test
    public void testEncodeNonAscii_longString() {
        StringBuilder input = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        
        // Create a long string with accented characters
        for (int i = 0; i < 1000; i++) {
            input.append("café");
            expected.append("cafe");
        }
        
        String result = StrUtil.encodeNonAscii(input.toString());
        assertEquals("Long string should be processed correctly", expected.toString(), result);
    }

    @Test
    public void testEncodeNonAscii_singleCharacter() {
        assertEquals("Single accented character", "a", StrUtil.encodeNonAscii("á"));
        assertEquals("Single ASCII character", "a", StrUtil.encodeNonAscii("a"));
        assertEquals("Single number", "1", StrUtil.encodeNonAscii("1"));
        assertEquals("Single symbol", "!", StrUtil.encodeNonAscii("!"));
    }

    @Test
    public void testEncodeNonAscii_edgeCases() {
        // Test characters that might not be handled
        String input = "αβγδε";
        String result = StrUtil.encodeNonAscii(input);
        // Greek letters might not be converted, so we just ensure no exception is thrown
        assertNotNull("Greek letters should not cause null result", result);
        
        // Test emoji (if any)
        String emojiInput = "Hello 😀 World";
        String emojiResult = StrUtil.encodeNonAscii(emojiInput);
        assertNotNull("Emoji should not cause null result", emojiResult);
    }

    @Test
    public void testEncodeNonAscii_performanceTest() {
        // Simple performance test to ensure method doesn't hang
        StringBuilder largeInput = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeInput.append("àáâãäåæçèéêëìíîïñòóôõöøœùúûüÿß");
        }
        
        long startTime = System.currentTimeMillis();
        String result = StrUtil.encodeNonAscii(largeInput.toString());
        long endTime = System.currentTimeMillis();
        
        assertNotNull("Large input should not return null", result);
        assertTrue("Method should complete in reasonable time", (endTime - startTime) < 5000); // 5 seconds max
    }

    @Test
    public void testEncodeNonAscii_consistentResults() {
        String input = "café résumé";
        String result1 = StrUtil.encodeNonAscii(input);
        String result2 = StrUtil.encodeNonAscii(input);
        
        assertEquals("Multiple calls should return consistent results", result1, result2);
    }

    @Test
    public void testEncodeNonAscii_immutability() {
        String original = "café";
        String originalCopy = new String(original);
        StrUtil.encodeNonAscii(original);
        
        assertEquals("Original string should not be modified", originalCopy, original);
    }
}