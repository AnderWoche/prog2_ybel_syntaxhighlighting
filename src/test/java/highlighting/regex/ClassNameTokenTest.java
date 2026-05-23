package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the class-name token: CamelCase identifiers (uppercase + at least one lowercase). */
class ClassNameTokenTest {

    private static final ColorType TYPE = ColorType.CLASS_NAME_COLOUR;

    @Test
    void matches_simpleClassName() {
        String text = "String";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("String", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleClassNames() {
        String text = "MyClass extends BaseClass implements SomeInterface";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("MyClass", slice(text, regions.get(0)));
        assertEquals("BaseClass", slice(text, regions.get(1)));
        assertEquals("SomeInterface", slice(text, regions.get(2)));
    }

    @Test
    void matches_atStartMiddleEnd() {
        String text = "Foo and Bar and Baz";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(text.length(), regions.get(2).end());
    }

    @Test
    void noMatch_lowercaseIdentifier() {
        assertTrue(match(TYPE, "foo bar baz").isEmpty());
    }

    @Test
    void noMatch_allUppercaseConstant() {
        // FOO has no lowercase → not a class name → constant pattern handles it elsewhere.
        assertTrue(match(TYPE, "FOO MAX_VALUE PI").isEmpty());
    }

    @Test
    void noMatch_singleUppercaseLetter() {
        // `T`, `K`, `V` look like generic type parameters; the pattern requires lowercase too.
        assertTrue(match(TYPE, "T K V").isEmpty());
    }

    @Test
    void noMatch_inEmptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void matches_classWithDigits() {
        // `Foo2Bar` — has lowercase and uppercase, valid identifier shape.
        String text = "Foo2Bar";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("Foo2Bar", slice(text, regions.getFirst()));
    }

    @Test
    void matches_doesNotMatchSubstring_ofLowercaseWord() {
        // `myClassWasHere` starts lowercase → not matched.
        assertTrue(match(TYPE, "myClassWasHere").isEmpty());
    }
}
