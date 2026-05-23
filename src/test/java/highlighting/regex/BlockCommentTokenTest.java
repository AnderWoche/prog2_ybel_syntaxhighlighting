package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the block-comment token: matches {@code /* ... *\/} (lazy, multi-line). */
class BlockCommentTokenTest {

    private static final ColorType TYPE = ColorType.BLOCK_COMMENT_COLOUR;

    @Test
    void matches_singleLineBlockComment() {
        String text = "/* simple */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("/* simple */", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multiLineBlockComment() {
        String text = "/*\n line 1\n line 2\n*/";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void matches_atStartMiddleEnd() {
        String text = "/*start*/ x; /*middle*/ y; /*end*/";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("/*start*/", slice(text, regions.get(0)));
        assertEquals("/*middle*/", slice(text, regions.get(1)));
        assertEquals("/*end*/", slice(text, regions.get(2)));
    }

    @Test
    void lazyMatching_doesNotJoinTwoComments() {
        // With a greedy regex this would become one giant match. Lazy `.*?` must stop at the first
        // `*/`.
        String text = "/* a */ between /* b */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(2, regions.size());
        assertEquals("/* a */", slice(text, regions.get(0)));
        assertEquals("/* b */", slice(text, regions.get(1)));
    }

    @Test
    void noMatch_unterminatedBlockComment() {
        // Has no closing `*/` — should not match anything.
        assertTrue(match(TYPE, "/* never closes").isEmpty());
    }

    @Test
    void noMatch_whenJustDivide() {
        assertTrue(match(TYPE, "int x = a / b;").isEmpty());
    }

    @Test
    void emptyBlockComment_isMatched() {
        String text = "/**/";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("/**/", slice(text, regions.getFirst()));
    }

    @Test
    void keywordLikeText_insideBlockComment_isMatched() {
        String text = "/* public class Foo extends Bar */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void blockComment_alsoMatchesJavadocSyntax_byItself() {
        // The block-comment regex deliberately also matches `/** ... */`. In production
        // the Javadoc token comes first in the list, so Javadoc wins via conflict resolution.
        // We document the underlying behaviour here.
        String text = "/** looks like javadoc */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }

    @Test
    void lineCommentSyntax_insideBlockComment_isPartOfBlock() {
        String text = "/* see // here it's fine */";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals(text, slice(text, regions.getFirst()));
    }
}
