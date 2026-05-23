package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the method-call token: matches {@code .name} when followed by {@code (}. */
class MethodCallTokenTest {

    private static final ColorType TYPE = ColorType.METHOD_CALL_COLOUR;

    @Test
    void matches_simpleCall() {
        String text = "obj.equals(other)";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("equals", slice(text, regions.getFirst()));
    }

    @Test
    void matches_chainedCalls() {
        // Both `getName` and `toString` are method calls.
        String text = "mc.getName().toString()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("getName", slice(text, regions.get(0)));
        assertEquals("toString", slice(text, regions.get(1)));
    }

    @Test
    void matches_systemOutPrintln() {
        // `out` is NOT followed by `(`, so it's not a call. Only `println` matches.
        String text = "System.out.println(\"hi\")";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("println", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_fieldAccessWithoutParens() {
        assertTrue(match(TYPE, "obj.field").isEmpty());
    }

    @Test
    void noMatch_constantAccess() {
        // `Math.PI` — `PI` not followed by `(`.
        assertTrue(match(TYPE, "Math.PI").isEmpty());
    }

    @Test
    void noMatch_freeStandingMethodCall() {
        // Calls without `.` prefix are not matched (avoids false positives on keywords, casts).
        assertTrue(match(TYPE, "create()").isEmpty());
    }

    @Test
    void matches_withWhitespaceBeforeParen() {
        String text = "obj.foo ()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("foo", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_emptyInput() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_atStartOfText_ifPrecededByDot() {
        // edge: `.foo(` at the very start
        String text = ".foo()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("foo", slice(text, regions.getFirst()));
    }
}
