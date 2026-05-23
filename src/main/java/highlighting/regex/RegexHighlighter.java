package highlighting.regex;

import highlighting.color.ColorResolver;
import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

    private final ColorResolver colorResolver;

    public RegexHighlighter(ColorResolver colorResolver) {
        this.colorResolver = colorResolver;
    }

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        ArrayList<HighlightRegion> regions = new ArrayList<>();

        for (Token token : MiniJavaTokens.defaultTokens()) {
            regions.addAll(token.test(text, colorResolver));
        }
        return regions;
    }

    @Override
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
        if (regions == null || regions.isEmpty()) return List.of();

        List<HighlightRegion> result = new ArrayList<>();
        result.add(regions.getFirst());

        for (int i = 1; i < regions.size(); i++) {
            HighlightRegion region = regions.get(i);
            if (region.start() < result.getLast().end()) continue;
            result.add(region);
        }

        return result;
    }
}
