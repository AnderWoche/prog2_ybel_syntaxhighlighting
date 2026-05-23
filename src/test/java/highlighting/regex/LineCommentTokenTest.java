package highlighting.regex;

import static highlighting.regex.TokenTestSupport.match;
import static highlighting.regex.TokenTestSupport.slice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.color.ColorType;
import highlighting.core.HighlightRegion;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the line-comment token: matches {@code //...} until end of line. */
class LineCommentTokenTest {

    private static final ColorType TYPE = ColorType.LINE_COMMENT_COLOUR;

    @Test
    void matches_atStartOfText() {
        String text = "// hello world";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// hello world", slice(text, regions.getFirst()));
    }

    @Test
    void matches_atEndOfText() {
        String text = "int x = 5; // trailing";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// trailing", slice(text, regions.getFirst()));
    }

    @Test
    void matches_inTheMiddleOfText() {
        String text = "before\n// middle\nafter";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// middle", slice(text, regions.getFirst()));
    }

    @Test
    void matches_multipleCommentsInOneText() {
        String text = "// first\nint x;\n// second\n// third";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(3, regions.size());
        assertEquals("// first", slice(text, regions.get(0)));
        assertEquals("// second", slice(text, regions.get(1)));
        assertEquals("// third", slice(text, regions.get(2)));
    }

    @Test
    void noMatch_whenNoLineComment() {
        assertTrue(match(TYPE, "int x = 5;").isEmpty());
    }

    @Test
    void noMatch_whenSingleSlash() {
        // A single slash is a divide operator, not a comment start.
        assertTrue(match(TYPE, "int x = a / b;").isEmpty());
    }

    @Test
    void stopsAtNewline() {
        String text = "// stop here\nnotComment";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// stop here", slice(text, regions.getFirst()));
    }

    @Test
    void emptyLineComment_isStillAComment() {
        String text = "//\nnext";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("//", slice(text, regions.getFirst()));
    }

    @Test
    void keywordLikeText_insideComment_isMatchedAsComment() {
        // The whole "// public class Foo" must be one comment match — the regex itself
        // doesn't know about keywords; that conflict is resolved later in the pipeline.
        String text = "// public class Foo";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// public class Foo", slice(text, regions.getFirst()));
    }

    @Test
    void blockCommentSyntax_insideLineComment_isPartOfTheLineComment() {
        String text = "// this /* is */ ignored as block";
        List<HighlightRegion> regions = match(TYPE, text);
        assertEquals(1, regions.size());
        assertEquals("// this /* is */ ignored as block", slice(text, regions.getFirst()));
    }
}
