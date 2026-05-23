package highlighting.regex;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for the annotation token: matches {@code @Word}. */
class AnnotationTokenTest {

    private static final ColorType TYPE = ColorType.ANNOTATION_COLOUR;

    @Test
    void matches_atLineStart() {
        String text = "@Override\npublic void create() {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("@Override", slice(text, regions.getFirst()));
    }

    @Test
    void matches_withLeadingWhitespace() {
        String text = "    @Override";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("@Override", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleAnnotationsInText() {
        String text = "@Override\n@Deprecated\n@SuppressWarnings";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("@Override", slice(text, regions.get(0)));
        assertEquals("@Deprecated", slice(text, regions.get(1)));
        assertEquals("@SuppressWarnings", slice(text, regions.get(2)));
    }

    @Test
    void matches_atVeryEnd() {
        String text = "class Foo {} @MyAnno";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text.length(), regions.getFirst().end());
    }

    @Test
    void stopsAtHyphen() {
        // `@Over-ride` is invalid as annotation. The regex only matches `@Over` because `-` is not \w.
        String text = "@Over-ride";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("@Over", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_atSignAlone() {
        // `@` without a following word char does not match (regex requires \w+).
        assertTrue(match(TYPE, "@ ").isEmpty());
    }

    @Test
    void noMatch_emailLikeText_inIsolation() {
        // `foo@bar` — the `@bar` part will match! This is a known limitation; the conflict
        // would normally not arise because emails appear inside strings, where the string
        // token wins. We document the standalone behaviour here.
        String text = "foo@bar";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("@bar", slice(text, regions.getFirst()));
    }

    @Test
    void noMatch_inEmptyText() {
        assertTrue(match(TYPE, "").isEmpty());
    }

    @Test
    void noMatch_inCodeWithoutAnnotations() {
        assertTrue(match(TYPE, "public class Foo extends Bar {}").isEmpty());
    }
}
