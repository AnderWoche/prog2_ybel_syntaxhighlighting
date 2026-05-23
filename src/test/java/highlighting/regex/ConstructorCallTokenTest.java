package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for the constructor-call token: matches the class name in {@code new Foo(} / {@code new
 * Foo<}.
 */
class ConstructorCallTokenTest {

    private static final ColorType TYPE = ColorType.CONSTRUCTOR_CALL_COLOUR;

    @Test
    void matches_simpleConstructor() {
        String text = "new Character('a')";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("Character", slice(text, regions.getFirst()));
    }

    @Test
    void matches_diamondConstructor() {
        String text = "new HashMap<>()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("HashMap", slice(text, regions.getFirst()));
    }

    @Test
    void matches_genericConstructor() {
        String text = "new ArrayList<String>()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("ArrayList", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleConstructorsInExpression() {
        String text = "new Foo(new Bar())";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("Foo", slice(text, regions.get(0)));
        assertEquals("Bar", slice(text, regions.get(1)));
    }

    @Test
    void matches_withExtraWhitespace() {
        String text = "new   SpriteBatch  ()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("SpriteBatch", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_lowercaseClassName() {
        // We only treat capitalised names as classes.
        assertTrue(match(TYPE, "new foo()").isEmpty());
    }

    @Test
    void noMatch_arrayCreation() {
        // `new Foo[10]` has `[` after the class name, not `<` or `(` — by design not matched.
        assertTrue(match(TYPE, "new Foo[10]").isEmpty());
    }

    @Test
    void noMatch_primitiveArrayCreation() {
        // `int` is lowercase.
        assertTrue(match(TYPE, "new int[5]").isEmpty());
    }

    @Test
    void noMatch_newAsIdentifierSubstring() {
        // `\bnew\b` boundary prevents matching inside `renewable`.
        assertTrue(match(TYPE, "renewable Foo()").isEmpty());
    }

    @Test
    void noMatch_classWithoutNew() {
        assertTrue(match(TYPE, "Character.MIN_VALUE").isEmpty());
    }

    @Test
    void matches_atStartOfText() {
        String text = "new MyClass()";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(4, regions.getFirst().start()); // after "new "
    }
}
