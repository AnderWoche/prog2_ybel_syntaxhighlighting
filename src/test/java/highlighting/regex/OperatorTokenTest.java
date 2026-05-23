package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the operator token: matches a contiguous sequence of operator characters. */
class OperatorTokenTest {

    private static final ColorType TYPE = ColorType.OPERATOR_COLOUR;

    @Test
    void matches_singleOperator() {
        String text = "+";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("+", slice(text, regions.getFirst()));
    }

    @Test
    void matches_compoundOperator_plusEquals() {
        String text = "+=";
        List<HighlightRegion> regions = match(TYPE, text);
        // Greedy match consumes both chars as one operator sequence.
        assertEquals(1, regions.size());
        assertEquals("+=", slice(text, regions.getFirst()));
    }

    @Test
    void matches_arrowOperator() {
        String text = "->";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("->", slice(text, regions.getFirst()));
    }

    @Test
    void matches_doubleEquals() {
        String text = "==";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("==", slice(text, regions.getFirst()));
    }

    @Test
    void matches_operatorsBetweenIdentifiers() {
        String text = "a + b - c";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("+", slice(text, regions.get(0)));
        assertEquals("-", slice(text, regions.get(1)));
    }

    @Test
    void noMatch_onlyIdentifiers() {
        assertTrue(match(TYPE, "foo bar baz").isEmpty());
    }

    @Test
    void noMatch_emptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_atStartMiddleEnd() {
        String text = "+a-b*";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("+", slice(text, regions.get(0)));
        assertEquals("-", slice(text, regions.get(1)));
        assertEquals("*", slice(text, regions.get(2)));
    }

    @Test
    void doesNotMatch_parentheses() {
        // `(` `)` `{` `}` `[` `]` are NOT in our operator class.
        assertTrue(match(TYPE, "(){}[]").isEmpty());
    }

    @Test
    void doesNotMatch_dotOrSemicolon() {
        assertTrue(match(TYPE, "obj.field;").isEmpty());
    }
}
