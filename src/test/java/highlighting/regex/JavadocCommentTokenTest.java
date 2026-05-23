package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the Javadoc-comment token: matches {@code /** ... *\/}. */
class JavadocCommentTokenTest {

    private static final ColorType TYPE = ColorType.JAVADOC_COMMENT_COLOUR;

    @Test
    void matches_singleLineJavadoc() {
        String text = "/** one-liner */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_multiLineJavadocBlock() {
        String text = "/**\n * description\n * @param x foo\n */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleJavadocBlocks() {
        String text = "/** first */ int a; /** second */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("/** first */", slice(text, regions.get(0)));
        assertEquals("/** second */", slice(text, regions.get(1)));
    }

    @Test
    void noMatch_onPlainBlockComment() {
        // Regular block comments start with `/*` (one star) — Javadoc requires `/**`.
        assertTrue(match(TYPE, "/* not javadoc */").isEmpty());
    }

    @Test
    void noMatch_onLineComment() {
        assertTrue(match(TYPE, "// not javadoc").isEmpty());
    }

    @Test
    void noMatch_unterminatedJavadoc() {
        assertTrue(match(TYPE, "/** unterminated").isEmpty());
    }

    @Test
    void lazyMatching_doesNotJoinTwoJavadocBlocks() {
        String text = "/** a */ x; /** b */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("/** a */", slice(text, regions.get(0)));
        assertEquals("/** b */", slice(text, regions.get(1)));
    }

    @Test
    void keywordsAndAnnotationsInsideJavadoc_areMatchedAsJavadoc() {
        String text = "/** describes @Override which is a public static method */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_atVeryStartOfText() {
        String text = "/** hi */ class Foo {}";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(0, regions.getFirst().start());
    }

    @Test
    void matches_atVeryEndOfText() {
        String text = "class Foo {} /** end */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text.length(), regions.getFirst().end());
    }
}
