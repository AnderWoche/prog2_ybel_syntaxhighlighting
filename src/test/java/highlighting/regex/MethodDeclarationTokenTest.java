package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the method-declaration token: matches only the method name in declarations. */
class MethodDeclarationTokenTest {

    private static final ColorType TYPE = ColorType.METHOD_NAME_COLOUR;

    @Test
    void matches_publicVoidMethod() {
        String text = "public void create() {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        // Only the method NAME is highlighted, not the modifiers/type.
        assertEquals("create", slice(text, regions.getFirst()));
    }

    @Test
    void matches_privateMethodWithClassReturnType() {
        String text = "private String getName() { return name; }";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("getName", slice(text, regions.getFirst()));
    }

    @Test
    void matches_publicStaticVoid_main() {
        String text = "public static void main(String[] args) {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("main", slice(text, regions.getFirst()));
    }

    @Test
    void matches_protectedFinalMethod() {
        String text = "protected final Result compute() {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("compute", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleMethodsInOneText() {
        String text = "public void a() {} private void b() {} protected String c() {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("a", slice(text, regions.get(0)));
        assertEquals("b", slice(text, regions.get(1)));
        assertEquals("c", slice(text, regions.get(2)));
    }

    @Test
    void noMatch_withoutVisibilityModifier() {
        // Package-private methods (no modifier) are intentionally not matched — too risky.
        assertTrue(match(TYPE, "void create() {}").isEmpty());
    }

    @Test
    void noMatch_lowercaseReturnType_notVoidNotClass() {
        // `int` is lowercase and not `void`, and not in our type alternative — won't match.
        assertTrue(match(TYPE, "public int foo() {}").isEmpty());
    }

    @Test
    void noMatch_onMethodCall() {
        // `obj.create()` is a CALL, not a declaration.
        assertTrue(match(TYPE, "obj.create()").isEmpty());
    }

    @Test
    void noMatch_onPlainText() {
        assertTrue(match(TYPE, "this is just text").isEmpty());
    }

    @Test
    void matches_acrossLineBreak() {
        // \s+ matches newlines, so signatures spanning two lines still work.
        String text = "public void\ncreate()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("create", slice(text, regions.getFirst()));
    }
}
