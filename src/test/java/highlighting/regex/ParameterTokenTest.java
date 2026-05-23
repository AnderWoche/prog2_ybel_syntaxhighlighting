package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the parameter token: matches the NAME inside method-signature parameter lists. */
class ParameterTokenTest {

    private static final ColorType TYPE = ColorType.PARAMETER_COLOUR;

    @Test
    void matches_singleParameter() {
        String text = "foo(String name)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("name", slice(text, regions.getFirst()));
    }

    @Test
    void matches_twoParameters() {
        String text = "(String defaultText, SyntaxHighlighter highlighter)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("defaultText", slice(text, regions.get(0)));
        assertEquals("highlighter", slice(text, regions.get(1)));
    }

    @Test
    void matches_threeParameters() {
        String text = "(int a, int b, int c)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("a", slice(text, regions.get(0)));
        assertEquals("b", slice(text, regions.get(1)));
        assertEquals("c", slice(text, regions.get(2)));
    }

    @Test
    void noMatch_emptyParameterList() {
        assertTrue(match(TYPE, "create()").isEmpty());
    }

    @Test
    void noMatch_methodCallWithSingleArgument() {
        // `foo(arg)` — only one "word" between `(` and `)`, regex needs `type name`.
        assertTrue(match(TYPE, "foo(arg)").isEmpty());
    }

    @Test
    void noMatch_fieldDeclaration() {
        // Fields are not in parentheses, so no `(` or `,` precedes them.
        assertTrue(match(TYPE, "private String name;").isEmpty());
    }

    @Test
    void noMatch_localVariable() {
        assertTrue(match(TYPE, "String s = \"hi\";").isEmpty());
    }

    @Test
    void matches_inMultilineSignature() {
        String text = "foo(\n  String a,\n  int b\n)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("a", slice(text, regions.get(0)));
        assertEquals("b", slice(text, regions.get(1)));
    }

    @Test
    void noMatch_inEmptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_constructorSignature() {
        String text = "LibgdxSetup(MainController mc)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("mc", slice(text, regions.getFirst()));
    }
}
