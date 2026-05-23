package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the char-literal token: matches {@code 'x'} or {@code '\\n'}. */
class CharLiteralTokenTest {

    private static final ColorType TYPE = ColorType.CHAR_LITERAL_COLOUR;

    @Test
    void matches_simpleChar() {
        String text = "'a'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("'a'", slice(text, regions.getFirst()));
    }

    @Test
    void matches_digitChar() {
        String text = "'5'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("'5'", slice(text, regions.getFirst()));
    }

    @Test
    void matches_spaceChar() {
        String text = "' '";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("' '", slice(text, regions.getFirst()));
    }

    @Test
    void matches_escapedChar_newline() {
        // Java source for: '\n'
        String text = "'\\n'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_escapedChar_singleQuote() {
        // Java source for: '\''
        String text = "'\\''";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_escapedChar_backslash() {
        // Java source for: '\\'
        String text = "'\\\\'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleChars_inOneText() {
        String text = "'a' + 'b' + 'c'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("'a'", slice(text, regions.get(0)));
        assertEquals("'b'", slice(text, regions.get(1)));
        assertEquals("'c'", slice(text, regions.get(2)));
    }

    @Test
    void noMatch_emptyChar() {
        // '' is not a valid char literal: the regex requires exactly one inner character.
        assertTrue(match(TYPE, "''").isEmpty());
    }

    @Test
    void noMatch_multiCharContent() {
        // 'abc' has too many chars — the regex expects exactly one char (or one escape).
        assertTrue(match(TYPE, "'abc'").isEmpty());
    }

    @Test
    void noMatch_unterminatedChar() {
        assertTrue(match(TYPE, "'a").isEmpty());
    }

    @Test
    void matchAtStart() {
        String text = "'x' = value";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(0, regions.getFirst().start());
    }

    @Test
    void matchAtEnd() {
        String text = "value = 'x'";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text.length(), regions.getFirst().end());
    }
}
