package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the constant token: ALL_CAPS identifiers (with digits and underscores allowed). */
class ConstantTokenTest {

    private static final ColorType TYPE = ColorType.CONSTANT_COLOUR;

    @Test
    void matches_simpleConstant() {
        String text = "MAX_VALUE";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("MAX_VALUE", slice(text, regions.getFirst()));
    }

    @Test
    void matches_shortConstant() {
        String text = "PI";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("PI", slice(text, regions.getFirst()));
    }

    @Test
    void matches_monospacedFromFontField() {
        String text = "Font.MONOSPACED";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("MONOSPACED", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleConstants() {
        String text = "MIN_VALUE + MAX_VALUE + DEFAULT";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("MIN_VALUE", slice(text, regions.get(0)));
        assertEquals("MAX_VALUE", slice(text, regions.get(1)));
        assertEquals("DEFAULT", slice(text, regions.get(2)));
    }

    @Test
    void matches_constantWithDigits() {
        String text = "HTTP_404";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("HTTP_404", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_camelCaseClassName() {
        // `MyClass` has lowercase → constant pattern (all-caps) doesn't match.
        assertTrue(match(TYPE, "MyClass String Foo").isEmpty());
    }

    @Test
    void noMatch_lowercaseIdentifier() {
        assertTrue(match(TYPE, "myVar count value").isEmpty());
    }

    @Test
    void noMatch_inEmptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_atStartMiddleEnd() {
        String text = "MIN x MAX y END";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(2).end());
    }
}
