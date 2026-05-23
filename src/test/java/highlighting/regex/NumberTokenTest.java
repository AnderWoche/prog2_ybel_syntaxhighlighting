package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the number token: integers and decimals as whole words. */
class NumberTokenTest {

    private static final ColorType TYPE = ColorType.NUMBER_COLOUR;

    @Test
    void matches_integer() {
        String text = "42";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("42", slice(text, regions.getFirst()));
    }

    @Test
    void matches_decimal() {
        String text = "3.14";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("3.14", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleNumbersInExpression() {
        String text = "x = 1 + 2 * 3";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("1", slice(text, regions.get(0)));
        assertEquals("2", slice(text, regions.get(1)));
        assertEquals("3", slice(text, regions.get(2)));
    }

    @Test
    void matches_numberAtStartMiddleEnd() {
        String text = "1 plus 2 equals 3";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(2).end());
    }

    @Test
    void noMatch_digitInsideIdentifier() {
        // `x123` — no word boundary between x and 1, so the leading digits don't form a number.
        assertTrue(match(TYPE, "x123").isEmpty());
    }

    @Test
    void noMatch_letterAfterDigits_breaksOnly_inMiddle() {
        // `123abc` — `\b\d+\b` needs a word boundary after the digits; between `3` and `a` both
        // are word chars → no boundary → no match.
        assertTrue(match(TYPE, "123abc").isEmpty());
    }

    @Test
    void noMatch_onPlainIdentifier() {
        assertTrue(match(TYPE, "foo").isEmpty());
    }

    @Test
    void noMatch_onLeadingDot_aloneIsNotNumber() {
        // ".5" — pattern requires digits before the optional ".\d+"; matches just "5", not ".5".
        String text = ".5";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("5", slice(text, regions.getFirst()));
    }

    @Test
    void matches_numbersSeparatedByCommas() {
        String text = "[1, 2, 3, 4]";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(4, regions.size());
    }

    @Test
    void matches_zero() {
        String text = "0";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("0", slice(text, regions.getFirst()));
    }
}
