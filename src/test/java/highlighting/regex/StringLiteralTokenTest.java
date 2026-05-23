package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the string-literal token: matches {@code "..."} with escape support. */
class StringLiteralTokenTest {

    private static final ColorType TYPE = ColorType.STRING_LITERAL_COLOUR;

    @Test
    void matches_simpleString() {
        String text = "\"hello\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("\"hello\"", slice(text, regions.getFirst()));
    }

    @Test
    void matches_emptyString() {
        String text = "\"\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("\"\"", slice(text, regions.getFirst()));
    }

    @Test
    void matches_stringAtStartMiddleEnd() {
        // Java source for: "a" foo "b" bar "c"
        String text = "\"a\" foo \"b\" bar \"c\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("\"a\"", slice(text, regions.get(0)));
        assertEquals("\"b\"", slice(text, regions.get(1)));
        assertEquals("\"c\"", slice(text, regions.get(2)));
    }

    @Test
    void matches_stringWithEscapedQuote() {
        // Java source for: "say \"hi\""
        String text = "\"say \\\"hi\\\"\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        // The match covers the whole literal — the escaped \" does NOT end the string.
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_stringWithBackslashEscape() {
        // Java source for: "a\\b"
        String text = "\"a\\\\b\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_stringContainingLineCommentSyntax() {
        // Java source for: "url = http://example.com"
        String text = "\"url = http://example.com\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_stringContainingBlockCommentSyntax() {
        // Java source for: "wrap /* not */ inside"
        String text = "\"wrap /* not */ inside\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_stringContainingKeywords() {
        String text = "\"public class Foo\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_unterminatedString() {
        // Java source for: "no closing
        String text = "\"no closing";
        assertTrue(match(TYPE, text).isEmpty());
    }

    @Test
    void noMatch_whenNoQuotes() {
        assertTrue(match(TYPE, "plain identifier").isEmpty());
    }

    @Test
    void twoSeparateStrings_areTwoMatches() {
        // Java source for: "a" + "b"
        String text = "\"a\" + \"b\"";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("\"a\"", slice(text, regions.get(0)));
        assertEquals("\"b\"", slice(text, regions.get(1)));
    }
}
