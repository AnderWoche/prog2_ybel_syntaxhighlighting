package highlighting.antlr;

import highlighting.color.ColorResolver;
import highlighting.color.ColorType;
import highlighting.color.MiniJavaColours;
import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;

// This highlighter uses the ANTLR-generated MiniJavaLexer to turn the input text into a token
// stream. {@code collectMatches(String)} is the only method you need to implement: extract tokens
// of interest and map them to {@code HighlightRegions} using the colours from {@code
// MiniJavaColours}. Sorting, filtering of invalid regions, and conflict handling are performed by
// the base class {@code SyntaxHighlighter} via the template method {@code computeRegions(...)}.
public class AntlrTokenCollector extends SyntaxHighlighter {

    private final ColorResolver colorResolver;

    public AntlrTokenCollector(ColorResolver colorResolver) {
        this.colorResolver = colorResolver;
    }

    @Override
    public List<HighlightRegion> collectMatches(String text) {
        ArrayList<HighlightRegion> result = new ArrayList<>();

        MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromString(text));

        Token previous = null;
        Token token = lexer.nextToken();

        while(token.getType() != Token.EOF) {
            ColorType colorType = ColorType.colorFor(token.getType());
            if(colorType != null) {
                result.add(new HighlightRegion(
                    token.getStartIndex(),
                    token.getStopIndex() + 1,
                    colorResolver.resolveColor(colorType)
                    ));
            }

            if (previous != null
                && previous.getType() == MiniJavaLexer.AT
                && token.getType() == MiniJavaLexer.IDENTIFIER) {
                result.add(new HighlightRegion(
                    token.getStartIndex(),
                    token.getStopIndex() + 1,
                    colorResolver.resolveColor(ColorType.ANNOTATION_COLOUR)));
            }

            previous = token;


            token =  lexer.nextToken();
        }

        return result;
    }

    @Override
    public List<HighlightRegion> resolveConflicts(List<HighlightRegion> normalized) {
        if (normalized.isEmpty()) return normalized;

        List<HighlightRegion> result = new ArrayList<>();
        for (int i = 0; i < normalized.size(); i++) {
            HighlightRegion current = normalized.get(i);
            if (i + 1 < normalized.size()) {
                HighlightRegion next = normalized.get(i + 1);
                if (next.start() == current.start() && next.end() == current.end()) {
                    continue;
                }
            }
            result.add(current);
        }
        return result;
    }
}
