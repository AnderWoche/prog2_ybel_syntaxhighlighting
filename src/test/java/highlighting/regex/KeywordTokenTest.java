package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the keyword token: matches Java reserved words as whole words. */
class KeywordTokenTest {

    private static final ColorType TYPE = ColorType.KEYWORD_COLOUR;

    @Test
    void matches_singleKeyword() {
        String text = "class";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("class", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleKeywordsInOneLine() {
        String text = "public static final void";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(4, regions.size());
        assertEquals("public", slice(text, regions.get(0)));
        assertEquals("static", slice(text, regions.get(1)));
        assertEquals("final", slice(text, regions.get(2)));
        assertEquals("void", slice(text, regions.get(3)));
    }

    @Test
    void matches_keywordsAtStartMiddleEnd() {
        String text = "public Foo extends Bar implements Baz";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("public", slice(text, regions.get(0)));
        assertEquals("extends", slice(text, regions.get(1)));
        assertEquals("implements", slice(text, regions.get(2)));
    }

    @Test
    void matches_literalKeywords_trueFalseNull() {
        String text = "if (x == null) return true; else return false;";
        List<HighlightRegion> regions = match(TYPE, text);
        // expected: if, null, return, true, else, return, false
        assertEquals(7, regions.size());
        assertEquals("if", slice(text, regions.get(0)));
        assertEquals("null", slice(text, regions.get(1)));
        assertEquals("return", slice(text, regions.get(2)));
        assertEquals("true", slice(text, regions.get(3)));
        assertEquals("else", slice(text, regions.get(4)));
        assertEquals("return", slice(text, regions.get(5)));
        assertEquals("false", slice(text, regions.get(6)));
    }

    @Test
    void noMatch_keywordAsSubstring_classroom() {
        // `\b` boundaries prevent matching `class` inside `classroom`.
        assertTrue(match(TYPE, "classroom").isEmpty());
    }

    @Test
    void noMatch_keywordAsSubstring_intArray() {
        // `int` should NOT match inside `printArr`, `winter`, `interface` ... well, `interface` IS a keyword.
        assertTrue(match(TYPE, "printer winter sprint").isEmpty());
    }

    @Test
    void noMatch_capitalisedKeyword() {
        // Java is case-sensitive; `Class` is not a keyword.
        assertTrue(match(TYPE, "Class Public Void").isEmpty());
    }

    @Test
    void noMatch_onPlainIdentifiers() {
        assertTrue(match(TYPE, "foo bar baz").isEmpty());
    }

    @Test
    void matches_atVeryStartAndEnd() {
        String text = "public x return";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(1).end());
    }
}
