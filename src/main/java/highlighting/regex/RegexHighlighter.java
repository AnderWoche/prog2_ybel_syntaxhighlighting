package highlighting.regex;

import highlighting.color.ColorResolver;
import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;

import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

    private final ColorResolver colorResolver;

    public RegexHighlighter(ColorResolver colorResolver) {
        this.colorResolver = colorResolver;
    }

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        return List.of();
    }

    @Override
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
        return List.of();
    }
}
