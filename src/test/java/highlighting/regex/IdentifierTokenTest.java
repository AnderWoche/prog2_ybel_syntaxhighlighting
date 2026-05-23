package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the identifier token: matches any Java-style identifier. This pattern is intentionally
 * permissive — keywords, class names, constants etc. are filtered out later by token ORDER in
 * {@code MiniJavaTokens.defaultTokens()}.
 */
class IdentifierTokenTest {

    private static final ColorType TYPE = ColorType.IDENTIFIER_COLOUR;

    @Test
    void matches_simpleIdentifier() {
        String text = "name";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("name", slice(text, regions.getFirst()));
    }

    @Test
    void matches_identifierWithDigits() {
        String text = "foo42";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("foo42", slice(text, regions.getFirst()));
    }

    @Test
    void matches_identifierWithUnderscore() {
        String text = "_private";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("_private", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleIdentifiers() {
        String text = "foo bar baz";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("foo", slice(text, regions.get(0)));
        assertEquals("bar", slice(text, regions.get(1)));
        assertEquals("baz", slice(text, regions.get(2)));
    }

    @Test
    void matches_keywordsTooByItself() {
        // Important: in isolation the identifier pattern ALSO matches keywords like `class`.
        // The keyword token wins via list-order in the production pipeline; this test
        // documents the underlying permissive behaviour.
        String text = "class";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("class", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_onlyDigits() {
        // `123` starts with a digit — not a valid identifier start.
        assertTrue(match(TYPE, "123").isEmpty());
    }

    @Test
    void noMatch_onlyOperators() {
        assertTrue(match(TYPE, "+-*/").isEmpty());
    }

    @Test
    void noMatch_emptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_atStartMiddleEnd() {
        String text = "start middle end";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(2).end());
    }

    @Test
    void doesNotIncludeDigitsLeadingTheIdentifier() {
        // `9abc` — `\b` between space/end and `9`, then `[A-Za-z_]` fails on `9`.
        // So no match starts at 0. `abc` itself has no boundary before (between `9` and `a` is
        // word-to-word). No match overall.
        assertTrue(match(TYPE, "9abc").isEmpty());
    }
}
