package highlighting.regex;

import highlighting.color.ColorResolver;
import highlighting.color.MiniJavaColours;
import highlighting.color.WhiteModeColorResolver;
import highlighting.core.HighlightRegion;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link RegexHighlighter} — the naive "apply every token, then resolve overlaps" strategy.
 *
 * <p>Three layers are tested:
 * <ul>
 *   <li>{@code computeRegions} — full pipeline (collect → normalize → resolveConflicts),</li>
 *   <li>{@code collectMatches} — must return ALL matches, including overlapping ones,</li>
 *   <li>{@code resolveConflicts} — overlap logic on hand-crafted, already-sorted inputs.</li>
 * </ul>
 */
class RegexHighlighterTest {

    private final ColorResolver resolver = new WhiteModeColorResolver();
    private final RegexHighlighter highlighter = new RegexHighlighter(resolver);

    // ─────────────────────────────────────────────────────────────
    //  computeRegions — full pipeline / integration
    // ─────────────────────────────────────────────────────────────

    @Test
    void computeRegions_onEmptyText_isEmpty() {
        assertTrue(highlighter.computeRegions("").isEmpty());
    }

    @Test
    void computeRegions_onWhitespaceOnly_isEmpty() {
        assertTrue(highlighter.computeRegions("    \n  ").isEmpty());
    }

    @Test
    void computeRegions_simpleKeyword_isHighlightedAsKeyword() {
        List<HighlightRegion> regions = highlighter.computeRegions("public");
        assertEquals(1, regions.size());
        HighlightRegion r = regions.getFirst();
        assertEquals(0, r.start());
        assertEquals(6, r.end());
        assertEquals(MiniJavaColours.KEYWORD_COLOUR, r.colour());
    }

    @Test
    void computeRegions_keywordWinsOverIdentifier_atSameRegion() {
        // Both keyword and identifier patterns match "class". Keyword must win because it
        // appears first in MiniJavaTokens.defaultTokens() (stable sort + earlier in list).
        List<HighlightRegion> regions = highlighter.computeRegions("class");
        assertEquals(1, regions.size());
        assertEquals(MiniJavaColours.KEYWORD_COLOUR, regions.getFirst().colour());
    }

    @Test
    void computeRegions_keywordInsideLineComment_isMatchedAsComment() {
        // The line-comment token swallows the entire line; the keyword overlaps it and is dropped.
        String text = "// public class Foo";
        List<HighlightRegion> regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        HighlightRegion r = regions.getFirst();
        assertEquals(0, r.start());
        assertEquals(text.length(), r.end());
        assertEquals(MiniJavaColours.LINE_COMMENT_COLOUR, r.colour());
    }

    @Test
    void computeRegions_keywordInsideBlockComment_isMatchedAsBlock() {
        String text = "/* public class */";
        List<HighlightRegion> regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(MiniJavaColours.BLOCK_COMMENT_COLOUR, regions.getFirst().colour());
    }

    @Test
    void computeRegions_javadocBeatsBlockComment_atSameRegion() {
        // Both /\*.*?\*/ (block) and /\*\*.*?\*/ (javadoc) match this text. Javadoc wins via
        // its earlier position in the token list (stable sort in normalize).
        String text = "/** doc */";
        List<HighlightRegion> regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, regions.getFirst().colour());
    }

    @Test
    void computeRegions_consecutiveKeywords_areBothKept() {
        // "public" → [0,6), "void" → [7,11). Adjacent in code, never overlap → both kept.
        String text = "public void";
        List<HighlightRegion> regions = highlighter.computeRegions(text);
        assertEquals(2, regions.size());
        assertEquals(0, regions.get(0).start());
        assertEquals(6, regions.get(0).end());
        assertEquals(7, regions.get(1).start());
        assertEquals(11, regions.get(1).end());
    }

    @Test
    void computeRegions_stringContainingKeyword_isMatchedAsString() {
        String text = "\"return public\"";
        List<HighlightRegion> regions = highlighter.computeRegions(text);
        assertEquals(1, regions.size());
        assertEquals(MiniJavaColours.STRING_LITERAL_COLOUR, regions.getFirst().colour());
    }

    @Test
    void computeRegions_textWithoutAnyMatches_isEmpty() {
        // Just punctuation that no token matches: parentheses and semicolon
        // (operator pattern excludes them, as do all other patterns).
        assertTrue(highlighter.computeRegions("(){};").isEmpty());
    }

    // ─────────────────────────────────────────────────────────────
    //  collectMatches — must return everything, including duplicates
    // ─────────────────────────────────────────────────────────────

    @Test
    void collectMatches_returnsAllOverlappingMatches() {
        // For "class" both the keyword AND identifier tokens fire. collectMatches must NOT filter.
        List<HighlightRegion> regions = highlighter.collectMatches("class");
        assertTrue(regions.size() >= 2,
            "expected at least 2 overlapping matches (keyword + identifier), got " + regions.size());
    }

    @Test
    void collectMatches_onEmptyText_isEmpty() {
        assertTrue(highlighter.collectMatches("").isEmpty());
    }

    @Test
    void collectMatches_doesNotSort() {
        // We don't assert exact order, but we DO assert no exception and a non-empty result for
        // input that has both early and late matches — order is normalize()'s job, not ours.
        List<HighlightRegion> regions = highlighter.collectMatches("class void");
        assertTrue(regions.size() >= 2);
    }

    // ─────────────────────────────────────────────────────────────
    //  resolveConflicts — unit-tested with hand-built sorted inputs
    // ─────────────────────────────────────────────────────────────

    @Test
    void resolveConflicts_emptyList_isEmpty() {
        assertTrue(highlighter.resolveConflicts(List.of()).isEmpty());
    }

    @Test
    void resolveConflicts_singleRegion_isKept() {
        HighlightRegion only = new HighlightRegion(0, 5, Color.RED);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(only));
        assertEquals(1, result.size());
        assertSame(only, result.getFirst());
    }

    @Test
    void resolveConflicts_adjacentHalfOpenRegions_areBothKept() {
        // [0,5) and [5,10) touch at index 5 but don't overlap (end is exclusive).
        HighlightRegion a = new HighlightRegion(0, 5, Color.RED);
        HighlightRegion b = new HighlightRegion(5, 10, Color.BLUE);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(a, b));
        assertEquals(2, result.size());
        assertSame(a, result.get(0));
        assertSame(b, result.get(1));
    }

    @Test
    void resolveConflicts_overlappingRegions_secondIsDropped() {
        // [0,10) and [5,15) — second is later, gets dropped.
        HighlightRegion a = new HighlightRegion(0, 10, Color.RED);
        HighlightRegion b = new HighlightRegion(5, 15, Color.BLUE);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(a, b));
        assertEquals(1, result.size());
        assertSame(a, result.getFirst());
    }

    @Test
    void resolveConflicts_sameStart_longerWins_perNormalizeOrder() {
        // After normalize, longer comes first at the same start position.
        // resolveConflicts must keep that one and drop the shorter.
        HighlightRegion longer = new HighlightRegion(0, 10, Color.RED);
        HighlightRegion shorter = new HighlightRegion(0, 5, Color.BLUE);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(longer, shorter));
        assertEquals(1, result.size());
        assertSame(longer, result.getFirst());
    }

    @Test
    void resolveConflicts_nonOverlappingRegionsWithGap_areAllKept() {
        HighlightRegion a = new HighlightRegion(0, 5, Color.RED);
        HighlightRegion b = new HighlightRegion(10, 15, Color.GREEN);
        HighlightRegion c = new HighlightRegion(20, 25, Color.BLUE);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(a, b, c));
        assertEquals(3, result.size());
    }

    @Test
    void resolveConflicts_oneBigRegionSwallowsInnerRegions() {
        // A covers [0,30). B and C are inside it — both must be dropped.
        HighlightRegion a = new HighlightRegion(0, 30, Color.RED);
        HighlightRegion b = new HighlightRegion(5, 10, Color.GREEN);
        HighlightRegion c = new HighlightRegion(15, 25, Color.BLUE);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(a, b, c));
        assertEquals(1, result.size());
        assertSame(a, result.getFirst());
    }

    @Test
    void resolveConflicts_mixedScenario_keepsCorrectRegions() {
        // A=[0,10) kept; B=[5,8) dropped (inside A);
        // C=[10,15) kept (touches A at 10 but no overlap);
        // D=[12,20) dropped (overlaps C); E=[20,25) kept (touches C at 15... wait 20 > 15 so gap).
        HighlightRegion a = new HighlightRegion(0, 10, Color.RED);
        HighlightRegion b = new HighlightRegion(5, 8, Color.GREEN);
        HighlightRegion c = new HighlightRegion(10, 15, Color.BLUE);
        HighlightRegion d = new HighlightRegion(12, 20, Color.YELLOW);
        HighlightRegion e = new HighlightRegion(20, 25, Color.CYAN);
        List<HighlightRegion> result = highlighter.resolveConflicts(List.of(a, b, c, d, e));
        assertEquals(3, result.size());
        assertSame(a, result.get(0));
        assertSame(c, result.get(1));
        assertSame(e, result.get(2));
    }
}
